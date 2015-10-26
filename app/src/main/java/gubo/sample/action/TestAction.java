
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
 *
 */
public class TestAction extends gubo.slipwire.Action
{
    private static class Test
    {
        public String result;
    }

    private static class Result
    {
        public long ms;

        public Test test;
    }

    public interface _test {
        @GET( "/test" )
        public Observable<TestAction.Result> get(
                @Query( "delay" ) String delay,
                @Query( "debug" ) String debug
        );
    }

    private final CompositeSubscription compositesubscription = new CompositeSubscription();
    private final Latch latch = new Latch();
    private final EventBus eventbus;
    private final DataBus databus;
    private final int port;

    @Inject
    public TestAction( final Object origin,final EventBus eventbus,final DataBus databus,final int port ) throws IllegalArgumentException {
        super( origin );
        if ( (eventbus == null) || (databus == null) ) { throw new IllegalArgumentException(); }

        this.eventbus = eventbus;
        this.databus = databus;

        this.port = port;
    }

    @Override
    public void invoke() {
        if ( !latch.trip() ) { throw new IllegalStateException(); }

        DBG.v( "TestAction.invoke" );

        final Action1<Event> EA1 = new Action1<Event>() {
            @Override
            public void call( final Event event ) {
                if ( event instanceof CancelEvent ) {
                    final Object origin = event.getOrigin();
                    if ( origin == TestAction.this.getOrigin() ) {
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

        final Observer<TestAction.Result> observer = new Observer<TestAction.Result>() {
            @Override public void onNext( final TestAction.Result result ) {
                final TestData testdata = new TestData( origin );
                try {
                    testdata.result = result.test.result;
                } catch ( Exception x ) {
                    DBG.m( x );
                }
                databus.send( testdata );
            }
            @Override public void onCompleted() {
                compositesubscription.unsubscribe();
                databus.send( new EOD( origin ) );
            }
            @Override public void onError( Throwable x ) {
                DBG.m( x );
                compositesubscription.unsubscribe();
                databus.send( new EOD( origin ) );
            }
        };

        final String endpoint = "http://" + Util.getIPv4Address() + ":" + port;
        DBG.v( "TestAction.endpoint: " + endpoint );

        final RestAdapter restadapter = new RestAdapter.Builder()
                .setEndpoint( endpoint )
                .setConverter( new GsonConverter( gson ) )
                .setLogLevel( DBG.verbose ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE )
                .setLog( new AndroidLog( "DBG" ) )
                .build();

        final Subscription restsubscription = restadapter.create( _test.class )
                .get( "0", "false" )
                .subscribeOn( Schedulers.newThread() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( observer );

        compositesubscription.add( restsubscription );
    }

    @Override
    public void cancel() {
        databus.send( new EOD( origin ) );

        compositesubscription.unsubscribe();

        DBG.m( "TestAction.cancel" );
    }
}

