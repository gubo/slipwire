
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
 * http://dev.gubo.com/wiki
 */
public class FetchRandomWikiAction extends gubo.slipwire.Action
{
    private static final String ENDPOINT = "http://dev.gubo.com";

    static class Page
    {
        public String id;
        public String title;
        public String thumbnailurl;
    }

    static class Response
    {
        Page resultant;
    }

    public interface status {
        @GET( "/wiki" )
        public Observable<FetchRandomWikiAction.Response> get(
                @Query( "delay" ) String delay,
                @Query( "debug" ) String debug
        );
    }

    private final CompositeSubscription compositesubscription = new CompositeSubscription();
    private final Latch latch = new Latch();
    private final EventBus eventbus;
    private final DataBus databus;

    @Inject
    public FetchRandomWikiAction( final Object origin,final EventBus eventbus,final DataBus databus ) throws IllegalArgumentException {
        super( origin );
        if ( (eventbus == null) || (databus == null) ) { throw new IllegalArgumentException(); }

        this.eventbus = eventbus;
        this.databus = databus;
    }

    @Override
    public void invoke() {
        if ( !latch.trip() ) { throw new IllegalStateException(); }

        DBG.v( "FetchRandomWikiAction.invoke" );

        final Action1<Event> EA1 = new Action1<Event>() {
            @Override
            public void call( final Event event ) {
                if ( event instanceof CancelEvent ) {
                    final Object origin = event.getOrigin();
                    if ( origin == FetchRandomWikiAction.this.getOrigin() ) {
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

        final Observer<FetchRandomWikiAction.Response> observer = new Observer<FetchRandomWikiAction.Response>() {
            @Override public void onNext( final FetchRandomWikiAction.Response response ) {
                final RandomWikiData randomwikidata = new RandomWikiData( origin );
                try {
                    randomwikidata.id = response.resultant.id;
                    randomwikidata.title = response.resultant.title;
                    randomwikidata.thumbnailurl = response.resultant.thumbnailurl;
                } catch ( Exception x ) {
                    DBG.m( x );
                }
                databus.send( randomwikidata );
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
                .setEndpoint( FetchRandomWikiAction.ENDPOINT )
                .setConverter( new GsonConverter( gson ) )
                .setLogLevel( DBG.verbose ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE )
                .setLog( new AndroidLog( "DBG" ) )
                .build();

        final Subscription restsubscription = restadapter.create( status.class )
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
        DBG.m( "FetchRandomWikiAction.cancel" );
    }
}
















