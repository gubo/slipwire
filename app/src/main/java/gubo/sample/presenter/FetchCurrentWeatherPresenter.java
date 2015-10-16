
package gubo.sample.presenter;

import javax.inject.*;

import rx.*;
import rx.functions.*;
import rx.android.schedulers.*;

import gubo.slipwire.*;
import gubo.sample.data.*;
import gubo.sample.event.*;

/*
 * - send FetchCurrentWeatherEvent when fetch button_normal pressed
 * - update enabled status of fetch button_normal when receive data sourced by FetchCurrentWeatherEvent.class
 */
public class FetchCurrentWeatherPresenter implements Presenter,DataSource
{
    public interface FetchListener
    {
        public void onFetch();
    }

    public interface Display extends Presenter.Display,DataSink
    {
        public void setActive( boolean enabled );
        public void setFetchListener( FetchCurrentWeatherPresenter.FetchListener listener );
        public void release();
    }

    private final EventBus eventbus;
    private final DataBus databus;

    private FetchCurrentWeatherPresenter.Display display;
    private CurrentWeatherData currentweatherdata;
    private Subscription datasubscription;
    private boolean currentlyactive;

    @Inject // TODO ?
    public FetchCurrentWeatherPresenter( final EventBus eventbus, final DataBus databus ) throws IllegalArgumentException {
        if ( (eventbus == null) || (databus == null) ) { throw new IllegalArgumentException(); }

        DBG.m( "FetchCurrentWeatherPresenter" );

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
        DBG.v( "FetchCurrentWeatherPresenter.bind " + display );

        if ( this.display != null ) {
            this.display.setFetchListener( null );
            this.display.release();
        }

        this.display = ( FetchCurrentWeatherPresenter.Display)display;

        if ( this.display != null ) {
            final FetchCurrentWeatherPresenter.FetchListener listener = new FetchCurrentWeatherPresenter.FetchListener() {
                @Override public void onFetch() { FetchCurrentWeatherPresenter.this.onFetch(); }
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

        DBG.m( "FetchCurrentWeatherPresenter.release" );
    }

    @Override public Data getDataFor( final int position ) { return currentweatherdata; }
    @Override public void getReadyFor( final int position,final int count ) {}

    private void onFetch() {
        if ( !currentlyactive ) {
            eventbus.send( new PendingEvent( FetchCurrentWeatherPresenter.class,true ) );
            eventbus.send( new FetchCurrentWeatherEvent( FetchCurrentWeatherPresenter.class ) );
            setActive( true );
        } else {
            eventbus.send( new CancelEvent( FetchCurrentWeatherPresenter.class ) );
            eventbus.send( new PendingEvent( FetchCurrentWeatherPresenter.class,false ) );
            setActive( false );
        }
    }

    private void onData( final Data data ) {
        if ( data instanceof CurrentWeatherData ) {
            currentweatherdata = ( CurrentWeatherData)data;
            display.setItemCount( 1 );
            display.setPosition( 0 );
        } else if ( data instanceof EOD ) {
            if ( data.getOrigin() == FetchCurrentWeatherPresenter.class ) {
                eventbus.send( new PendingEvent( FetchCurrentWeatherPresenter.class,false ) );
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
