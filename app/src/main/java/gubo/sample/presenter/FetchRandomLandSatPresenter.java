
package gubo.sample.presenter;

import rx.*;
import rx.functions.*;
import rx.android.schedulers.*;

import gubo.slipwire.*;
import gubo.sample.data.*;
import gubo.sample.event.*;

/*
 *
 */
public class FetchRandomLandSatPresenter implements Presenter,DataSource
{
    public interface FetchListener
    {
        public void onFetch();
    }

    public interface Display extends Presenter.Display,DataSink
    {
        public void setActive( boolean enabled );
        public void setFetchListener( FetchRandomLandSatPresenter.FetchListener listener );
        public void release();
    }

    private final EventBus eventbus;
    private final DataBus databus;

    private FetchRandomLandSatPresenter.Display display;
    private RandomLandSatData currentlandsatdata;
    private Subscription datasubscription;
    private boolean currentlyactive;

    public FetchRandomLandSatPresenter( final EventBus eventbus, final DataBus databus ) {
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
    public <D extends Presenter.Display> void bind( D display ) {
        DBG.v( "FetchRandomLandSatPresenter.bind " + display );

        if ( this.display != null ) {
            this.display.setFetchListener( null );
            this.display.release();
        }

        this.display = ( FetchRandomLandSatPresenter.Display)display;

        if ( this.display != null ) {
            final FetchRandomLandSatPresenter.FetchListener listener = new FetchRandomLandSatPresenter.FetchListener() {
                @Override public void onFetch() { FetchRandomLandSatPresenter.this.onFetch(); }
            };
            this.display.setFetchListener( listener );
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
            display.setFetchListener( null );
            display.release();
        }

        DBG.v( "FetchRandomLandSatPresenter.release" );
    }

    @Override public Data getDataFor( final int position ) { return currentlandsatdata; }
    @Override public void getReadyFor( final int position,final int count ) {}

    private void onFetch() {
        if ( !currentlyactive ) {
            eventbus.send( new PendingEvent( FetchRandomLandSatPresenter.class,true ) );
            eventbus.send( new FetchRandomLandSatEvent( FetchRandomLandSatPresenter.class ) );
            setActive( true );
        } else {
            eventbus.send( new CancelEvent( FetchRandomLandSatPresenter.class ) );
            eventbus.send( new PendingEvent( FetchRandomLandSatPresenter.class,false ) );
            setActive( false );
        }
    }

    private void onData( final Data data ) {
        if ( data instanceof RandomLandSatData ) {
            currentlandsatdata = ( RandomLandSatData)data;
            refresh();
        } else if ( data instanceof EOD ) {
            if ( data.getOrigin() == FetchRandomLandSatPresenter.class ) {
                eventbus.send( new PendingEvent( FetchRandomLandSatPresenter.class,false ) );
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
