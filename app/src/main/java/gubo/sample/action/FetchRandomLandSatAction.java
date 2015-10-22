
package gubo.sample.action;

import javax.inject.*;

import rx.*;
import rx.functions.*;
import rx.schedulers.*;
import rx.subscriptions.*;
import rx.android.schedulers.*;

import retrofit.*;
import retrofit.http.*;
import retrofit.android.*;
import retrofit.converter.*;

import com.google.gson.*;

import gubo.slipwire.*;
import gubo.sample.data.*;
import gubo.sample.event.*;

/*
 * http://dev.gubo.com/misc/landsat
 */
public class FetchRandomLandSatAction extends gubo.slipwire.Action
{
    private static final String ENDPOINT = "http://dev.gubo.com";

    static class City
    {
        String geonameid;
        String name;
        String latitude;
        String longitude;
        String countrycode;
    }

    static class LandSat
    {
        String date;
        String url;
        String id;
    }

    static class Response
    {
        City city;
        LandSat landsat;
    }

    public interface randomlandsat {
        @GET( "/misc/randomlandsat" )
        public Observable<FetchRandomLandSatAction.Response> get(
                @Query( "delay" ) String delay,
                @Query( "debug" ) String debug
        );
    }

    private final CompositeSubscription compositesubscription = new CompositeSubscription();
    private final Latch latch = new Latch();
    private final EventBus eventbus;
    private final DataBus databus;

    @Inject
    public FetchRandomLandSatAction( final Object origin,final EventBus eventbus,final DataBus databus ) throws IllegalArgumentException {
        super( origin );
        if ( (eventbus == null) || (databus == null) ) { throw new IllegalArgumentException(); }

        this.eventbus = eventbus;
        this.databus = databus;
    }

    @Override
    public void invoke() {
        if ( !latch.trip() ) { throw new IllegalStateException(); }

        DBG.v( "FetchRandomLandSatAction.invoke" );

        final Action1<Event> EA1 = new Action1<Event>() {
            @Override
            public void call( final Event event ) {
                if ( event instanceof CancelEvent ) {
                    final Object origin = event.getOrigin();
                    if ( origin == FetchRandomLandSatAction.this.getOrigin() ) {
                        cancel();
                    }
                } else if ( event instanceof CancelAllEvent ) {
                    cancel();
                }
            }
        };
        final Subscription eventsubscription = eventbus.toObserverable().subscribeOn( AndroidSchedulers.mainThread() ).subscribe( EA1 );
        compositesubscription.add( eventsubscription );

        final Gson gson = new GsonBuilder().create();

        eventbus.send( new NetworkActivityEvent( FetchCurrentWeatherAction.class, true ) );

        final Observer<FetchRandomLandSatAction.Response> observer = new Observer<FetchRandomLandSatAction.Response>() {
            @Override public void onNext( final FetchRandomLandSatAction.Response response ) {
                final RandomLandSatData randomlandsatdata = new RandomLandSatData( origin );
                try {
                    randomlandsatdata.geonameid = response.city.geonameid;
                    randomlandsatdata.name = response.city.name;
                    randomlandsatdata.latitude = response.city.latitude;
                    randomlandsatdata.longitude = response.city.longitude;
                    randomlandsatdata.countrycode = response.city.countrycode;
                    randomlandsatdata.id = response.landsat.id;
                    randomlandsatdata.date = response.landsat.date;
                    randomlandsatdata.url = response.landsat.url;
                } catch ( Exception x ) {
                    DBG.m( x );
                }
                databus.send( randomlandsatdata );
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

        final RestAdapter restadapter = new RestAdapter.Builder()
                .setEndpoint( FetchRandomLandSatAction.ENDPOINT )
                .setConverter( new GsonConverter( gson ) )
                .setLogLevel( DBG.verbose ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE )
                .setLog( new AndroidLog( "DBG" ) )
                .build();

        final Subscription restsubscription = restadapter.create( randomlandsat.class )
                .get( "2500", "false" )
                .subscribeOn( Schedulers.newThread() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( observer );

        compositesubscription.add( restsubscription );
    }

    @Override
    public void cancel() {
        eventbus.send( new NetworkActivityEvent( FetchCurrentWeatherAction.class,false ) );
        databus.send( new EOD( origin ) );
        compositesubscription.unsubscribe();
        DBG.m( "FetchRandomLandSatAction.cancel" );
    }
}
















