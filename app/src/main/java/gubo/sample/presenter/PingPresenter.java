
package gubo.sample.presenter;

import javax.inject.*;

import rx.*;
import rx.functions.*;
import rx.android.schedulers.*;

import gubo.slipwire.*;
import gubo.sample.data.*;
import gubo.sample.event.*;

/*
 *
 */
public class PingPresenter implements Presenter,DataSource
{
    public interface JobListener
    {
        public void onJob();
    }

    public interface Display extends Presenter.Display,DataSink
    {
        public void setJobListener( PingPresenter.JobListener listener );
        public void setActive( boolean active );
        public void release();
    }

    private final EventBus eventbus;
    private final DataBus databus;

    private PingPresenter.Display display;
    private Subscription datasubscription;
    private PingData currentpingdata;
    private boolean currentlyactive;

    @Inject
    public PingPresenter( final EventBus eventbus, final DataBus databus ) throws IllegalArgumentException {
        if ( (eventbus == null) || (databus == null) ) { throw new IllegalArgumentException(); }

        this.eventbus = eventbus;
        this.databus = databus;

        final Action1<Data> DA1 = new Action1<Data>() {
            @Override public void call( final Data data ) { onData( data ); }
        };
        datasubscription = databus.toObserverable().subscribeOn( AndroidSchedulers.mainThread() ).subscribe( DA1 );

        currentlyactive = false;
    }

    @Override
    public <D extends Presenter.Display> void bind( final D display ) {
        DBG.v( "PingPresenter.bind " + display );

        if ( this.display != null ) {
            this.display.setJobListener( null );
            this.display.release();
        }

        this.display = ( PingPresenter.Display)display;

        if ( this.display != null ) {
            final PingPresenter.JobListener listener = new PingPresenter.JobListener() {
                @Override public void onJob() { PingPresenter.this.onJob(); }
            };
            this.display.setJobListener( listener );
        }

        refresh();
    }

    @Override
    public void release() {
        if ( datasubscription != null ) {
            datasubscription.unsubscribe();
            datasubscription = null;
        }

        if ( display != null ) {
            display.setJobListener( null );
            display.release();
        }

        DBG.v( "PingPresenter.release" );
    }

    @Override public Data getDataFor( final int position ) { return currentpingdata; }
    @Override public void getReadyFor( final int position,final int count ) {}

    private void onJob() {
        if ( !currentlyactive ) {
            eventbus.send( new PendingEvent( PingPresenter.class,true ) );
            eventbus.send( new PingEvent( PingPresenter.class ) );
            setActive( true );
        } else {
            eventbus.send( new CancelEvent( PingPresenter.class ) );
            eventbus.send( new PendingEvent( PingPresenter.class,false ) );
            setActive( false );
        }
    }

    private void onData( final Data data ) {
        if ( data instanceof PingData ) {
            currentpingdata = ( PingData)data;
            display.setItemCount( 1 );
            display.setPosition( 0 );
        } else if ( data instanceof EOD ) {
            if ( data.getOrigin() == PingPresenter.class ) {
                eventbus.send( new PendingEvent( PingPresenter.class,false ) );
                setActive( false );
            }
        }
    }

    private void setActive( final boolean active ) {
        currentlyactive = active;
        refresh();
    }

    private void refresh() {
        if ( display != null ) {
            display.setActive( currentlyactive );
            display.setItemCount( 1 );
            display.setPosition( 0 );
        }
    }
}
