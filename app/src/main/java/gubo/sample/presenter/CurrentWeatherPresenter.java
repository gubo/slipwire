
package gubo.sample.presenter;

import javax.inject.*;

import gubo.sample.data.CurrentWeatherData;
import rx.*;
import rx.functions.*;
import rx.android.schedulers.*;

import gubo.slipwire.*;

/*
 * - update display when receive CurrentWeatherData
 */
public class CurrentWeatherPresenter implements Presenter,DataSource
{
    public interface Display extends Presenter.Display,DataSink
    {
        public void release();
    }

    private final EventBus eventbus;
    private final DataBus databus;

    private CurrentWeatherPresenter.Display display;
    private CurrentWeatherData currentweatherdata;
    private Subscription datasubscription;

    @Inject // TODO ?
    public CurrentWeatherPresenter( final EventBus eventbus,final DataBus databus ) throws IllegalArgumentException {
        if ( (eventbus == null) || (databus == null) ) { throw new IllegalArgumentException(); }

        DBG.m( "CurrentWeatherPresenter" );

        this.eventbus = eventbus;
        this.databus = databus;

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
    public void release() {        if ( datasubscription != null ) {
        datasubscription.unsubscribe();
        datasubscription = null;
    }

        if ( display != null ) {
            display.release();
        }

        DBG.m( "CurrentWeatherPresenter.release" );

    }

    @Override public Data getDataFor( final int position ) { return currentweatherdata; }
    @Override public void getReadyFor( final int position,final int count ) {}

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
