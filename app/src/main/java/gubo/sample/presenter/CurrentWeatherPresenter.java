
package gubo.sample.presenter;

import javax.inject.*;

import rx.*;
import rx.functions.*;
import rx.android.schedulers.*;

import gubo.slipwire.*;
import gubo.sample.data.*;
import gubo.sample.event.*;

/*
 * - update display when receive CurrentWeatherData
 */
public class CurrentWeatherPresenter implements Presenter,DataSource
{
    public interface Display extends Presenter.Display,DataSink
    {
        public void prepare();
        public void release();
    }

    private final EventBus eventbus;
    private final DataBus databus;

    private CurrentWeatherPresenter.Display display;
    private CurrentWeatherData currentweatherdata;
    private Subscription eventsubscription;
    private Subscription datasubscription;

    @Inject // TODO ?
    public CurrentWeatherPresenter( final EventBus eventbus,final DataBus databus ) throws IllegalArgumentException {
        if ( (eventbus == null) || (databus == null) ) { throw new IllegalArgumentException(); }

        this.eventbus = eventbus;
        this.databus = databus;

        final Action1<Event> EA1 = new Action1<Event>() {
            @Override public void call( final Event event ) { onEvent( event ); }
        };
        eventsubscription = eventbus.toObserverable().subscribeOn( AndroidSchedulers.mainThread() ).subscribe( EA1 );

        final Action1<Data> DA1 = new Action1<Data>() {
            @Override public void call( final Data data ) { onData( data ); }
        };
        datasubscription = databus.toObserverable().subscribeOn( AndroidSchedulers.mainThread() ).subscribe( DA1 );

        currentweatherdata = new CurrentWeatherData( null );
        currentweatherdata.heading = "";
        currentweatherdata.temp = 0F;
    }

    @Override
    public <D extends Presenter.Display> void bind( D display ) {
        DBG.v( "CurrentWeatherPresenter.bind " + display );

        if ( this.display != null ) {
            this.display.release();
        }

        this.display = ( CurrentWeatherPresenter.Display)display;

        refresh();
    }

    @Override
    public void release() {
        if ( eventsubscription != null ) {
            eventsubscription.unsubscribe();
            eventsubscription = null;
        }

        if ( datasubscription != null ) {
            datasubscription.unsubscribe();
            datasubscription = null;
        }

        if ( display != null ) {
            display.release();
        }

        DBG.v( "CurrentWeatherPresenter.release" );

    }

    @Override public Data getDataFor( final int position ) { return currentweatherdata; }
    @Override public void getReadyFor( final int position,final int count ) {}

    private void onEvent( final Event event ) {
        if ( event instanceof PendingEvent ) {
            final Object origin = event.getOrigin();
            if ( origin == FetchCurrentWeatherPresenter.class ) {
                if ( display != null ) {
                    display.prepare();
                }
            }
        }
    }

    private void onData( final Data data ) {
        if ( data instanceof CurrentWeatherData ) {
            currentweatherdata = ( CurrentWeatherData)data;
            refresh();
        }
    }

    private void refresh() {
        if ( display != null ) {
            display.setPosition( 0 );
            display.setItemCount( 1 );
        }
    }
}
