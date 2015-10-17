
package gubo.sample.action;

import javax.inject.*;

import com.google.gson.*;

import retrofit.*;
import retrofit.http.*;
import retrofit.android.*;
import retrofit.converter.*;

import rx.*;
import rx.functions.*;
import rx.schedulers.*;
import rx.subscriptions.*;
import rx.android.schedulers.*;

import gubo.slipwire.*;
import gubo.sample.data.*;
import gubo.sample.event.*;

/*
 * http://openweathermap.org/current
 *
 * developer@gubo.com
 * developer@gubo.com
 * monkey2015
 * 0c8371fb9687b9651044cc8b73a8d709
 *
 * sherman oaks: lat=34.15 lon=-118.45
 * http://api.openweathermap.org/data/2.5/weather?appid=0c8371fb9687b9651044cc8b73a8d709&lat=<LAT>&lon=<LON>
 *
 * icons:
 * http://openweathermap.org/img/w/10d.png
 */
public class FetchCurrentWeatherAction extends gubo.slipwire.Action
{
    private static final String ENDPOINT = "http://api.openweathermap.org/data/2.5/";
    private static final String ICON = "http://openweathermap.org/img/w/";

    static class Weather
    {
        int id;
        String main;
        String description;
        String icon;
    }

    static class Main
    {
        float temp;
        int pressure;
        int humidity;
        float temp_min;
        float temp_max;
    }

    static class Response
    {
        Weather [] weather;
        Main main;
    }

    public interface status {
        @GET( "/weather" )
        public Observable<FetchCurrentWeatherAction.Response> get(
                @Query( "appid" ) String appid,
                @Query( "lat" ) String lat,
                @Query( "lon" ) String lon,
                @Query( "units" ) String units
        );
    }

    private final CompositeSubscription compositesubscription = new CompositeSubscription();
    private final Latch latch = new Latch();
    private final EventBus eventbus;
    private final DataBus databus;

    @Inject
    public FetchCurrentWeatherAction( final Object origin,final EventBus eventbus,final DataBus databus ) throws IllegalArgumentException {
        super( origin );
        if ( (eventbus == null) || (databus == null) ) { throw new IllegalArgumentException(); }

        this.eventbus = eventbus;
        this.databus = databus;
    }

    @Override
    public void invoke() throws IllegalStateException {
        if ( !latch.trip() ) { throw new IllegalStateException(); }

        DBG.v( "FetchCurrentWeatherAction.invoke" );

        final Action1<Event> EA1 = new Action1<Event>() {
            @Override
            public void call( final Event event ) {
                if ( event instanceof CancelEvent ) {
                    final Object origin = event.getOrigin();
                    if ( origin == FetchCurrentWeatherAction.this.getOrigin() ) {
                        cancel();
                    }
                }
            }
        };
        final Subscription eventsubscription = eventbus.toObserverable().subscribeOn( AndroidSchedulers.mainThread() ).subscribe( EA1 );
        compositesubscription.add( eventsubscription );

        final Gson gson = new GsonBuilder().create();

        eventbus.send( new NetworkActivityEvent( FetchCurrentWeatherAction.class,true ) );

        final Observer<FetchCurrentWeatherAction.Response> observer = new Observer<FetchCurrentWeatherAction.Response>() {
            @Override public void onNext( final FetchCurrentWeatherAction.Response response ) {
                final CurrentWeatherData currentweatherdata = new CurrentWeatherData( origin );
                try {
                    currentweatherdata.id = response.weather[ 0 ].id;
                    currentweatherdata.temp = response.main.temp;
                    currentweatherdata.humidity = response.main.humidity;
                    currentweatherdata.pressure = response.main.pressure;
                    currentweatherdata.heading = response.weather[ 0 ].main;
                    currentweatherdata.iconurl = ICON + response.weather[ 0 ].icon + ".png";
                } catch ( Exception x ) {
                    DBG.m( x );
                }
                databus.send( currentweatherdata );
            }
            @Override public void onCompleted() {
                compositesubscription.unsubscribe();
                eventbus.send( new NetworkActivityEvent( FetchCurrentWeatherAction.class,false ) );
                databus.send( new EOD( origin ) );
            }
            @Override public void onError( Throwable x ) {
                DBG.m( x );
                compositesubscription.unsubscribe();
                eventbus.send( new NetworkActivityEvent( FetchCurrentWeatherAction.class,false ) );
                databus.send( new EOD( origin ) );
            }
        };

        // TODO: asynchronously get gps location (lat,lon) and merge to call to current weather

        final RestAdapter restadapter = new RestAdapter.Builder()
                .setEndpoint( FetchCurrentWeatherAction.ENDPOINT )
                .setConverter( new GsonConverter( gson ) )
                .setLogLevel( DBG.verbose ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE )
                .setLog( new AndroidLog( "DBG" ) )
                .build();

        final Subscription restsubscription = restadapter.create( status.class )
                .get( "0c8371fb9687b9651044cc8b73a8d709","34.15","-118.45","metric" )
                .subscribeOn( Schedulers.newThread() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( observer );

        compositesubscription.add( restsubscription );
    }

    @Override
    public void cancel() {
        eventbus.send( new NetworkActivityEvent( FetchCurrentWeatherAction.class,false ) );
        compositesubscription.unsubscribe();
        DBG.m( "FetchCurrentWeatherAction.cancel" );
    }
}
