
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
public class FetchRandomWikiPresenter implements Presenter,DataSource
{
    public interface FetchListener
    {
        public void onFetch();
    }

    public interface Display extends Presenter.Display,DataSink
    {
        public void setActive( boolean enabled );
        public void setFetchListener( FetchRandomWikiPresenter.FetchListener listener );
        public void release();
    }

    private final EventBus eventbus;
    private final DataBus databus;

    private FetchRandomWikiPresenter.Display display;
    private RandomWikiData currentwikidata;
    private Subscription datasubscription;
    private boolean currentlyactive;

    public FetchRandomWikiPresenter( final EventBus eventbus, final DataBus databus ) {
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
        DBG.v( "FetchRandomWikiPresenter.bind " + display );

        if ( this.display != null ) {
            this.display.setFetchListener( null );
            this.display.release();
        }

        this.display = ( FetchRandomWikiPresenter.Display)display;

        if ( this.display != null ) {
            final FetchRandomWikiPresenter.FetchListener listener = new FetchRandomWikiPresenter.FetchListener() {
                @Override public void onFetch() { FetchRandomWikiPresenter.this.onFetch(); }
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

        DBG.v( "FetchRandomWikiPresenter.release" );
    }

    @Override public Data getDataFor( final int position ) { return currentwikidata; }
    @Override public void getReadyFor( final int position,final int count ) {}

    private void onFetch() {
        if ( !currentlyactive ) {
            eventbus.send( new PendingEvent( FetchRandomWikiPresenter.class,true ) );
            eventbus.send( new FetchRandomWikiEvent( FetchRandomWikiPresenter.class ) );
            setActive( true );
        } else {
            eventbus.send( new CancelEvent( FetchRandomWikiPresenter.class ) );
            eventbus.send( new PendingEvent( FetchRandomWikiPresenter.class,false ) );
            setActive( false );
        }
    }

    private void onData( final Data data ) {
        if ( data instanceof RandomWikiData ) {
            currentwikidata = ( RandomWikiData)data;
            refresh();
        } else if ( data instanceof EOD ) {
            if ( data.getOrigin() == FetchRandomWikiPresenter.class ) {
                eventbus.send( new PendingEvent( FetchRandomWikiPresenter.class,false ) );
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
