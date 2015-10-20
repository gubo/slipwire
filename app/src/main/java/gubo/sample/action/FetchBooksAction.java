
package gubo.sample.action;

import com.google.gson.*;

import rx.*;
import rx.functions.*;
import rx.schedulers.Schedulers;
import rx.subscriptions.*;
import rx.android.schedulers.*;

import retrofit.*;
import retrofit.http.*;
import retrofit.android.*;
import retrofit.converter.*;

import gubo.slipwire.*;
import gubo.sample.data.*;
import gubo.sample.event.*;

/*
 *
 */
public class FetchBooksAction extends gubo.slipwire.Action
{
    private static final String ENDPOINT = "http://dev.gubo.com";

    private static class _Book
    {
        public String id;
        public int index;
        public String title;
        public String thumbnailurl;
    }

    private static class _Books
    {
        public int start;
        public int count;
        public _Book [] books;
    }

    private static class Response
    {
        public _Books resultant;
    }

    public interface infinitebooks {
        @GET( "/infinite/books" )
        public Observable<FetchBooksAction.Response> get(
                @retrofit.http.Query( "delay" ) String delay,
                @retrofit.http.Query( "debug" ) String debug,
                @retrofit.http.Query( "start" ) String start,
                @retrofit.http.Query( "count" ) String count
        );
    }

    private final CompositeSubscription compositesubscription = new CompositeSubscription();
    private final Latch latch = new Latch();
    private final EventBus eventbus;
    private final DataBus databus;
    private final int start;
    private final int count;

    public FetchBooksAction( final Object origin,final EventBus eventbus,final DataBus databus,final int start, final int count ) throws IllegalArgumentException {
        super( origin );
        if ( (eventbus == null) || (databus == null) ) { throw new IllegalArgumentException(); }

        this.eventbus = eventbus;
        this.databus = databus;
        this.start = start;
        this.count = count;
    }

    @Override
    public void invoke() {
        if ( !latch.trip() ) { throw new IllegalStateException(); }

        DBG.v( "FetchBooksAction.invoke" );

        final Action1<Event> EA1 = new Action1<Event>() {
            @Override
            public void call( final Event event ) {
                if ( event instanceof CancelEvent ) {
                    final Object origin = event.getOrigin();
                    if ( origin == FetchBooksAction.this.getOrigin() ) {
                        cancel();
                    }
                } else if ( event instanceof CancelAllEvent ) {
                    cancel();
                }
            }
        };
        final Subscription eventsubscription = eventbus.toObserverable().subscribeOn( AndroidSchedulers.mainThread() ).subscribe( EA1 );
        compositesubscription.add( eventsubscription );

        final Observer<FetchBooksAction.Response> observer = new Observer<FetchBooksAction.Response>() {
            @Override public void onNext( final FetchBooksAction.Response response ) {
                final Books books = new Books( origin );
                books.books = new Book[ 0 ];
                if ( (response.resultant != null) && (response.resultant.books != null) ) {
                    books.start = response.resultant.start;
                    books.count = response.resultant.count;
                    books.books = new Book[ response.resultant.books.length ];
                    int n = 0;
                    for ( final _Book _book : response.resultant.books ) {
                        if ( _book == null ) { continue; }
                        final Book book = new Book( origin );
                        book.id = _book.id;
                        book.index = _book.index;
                        book.title = _book.title;
                        book.thumbnailurl = _book.thumbnailurl;
                        books.books[ n ] = book;
                        n++;
                    }
                }
                databus.send( books );
            }
            @Override public void onCompleted() {
                compositesubscription.unsubscribe();
                eventbus.send( new NetworkActivityEvent( FetchBooksAction.class,false ) );
                databus.send( new EOD( origin ) );
            }
            @Override public void onError( Throwable x ) {
                DBG.m( x );
                compositesubscription.unsubscribe();
                eventbus.send( new NetworkActivityEvent( FetchBooksAction.class,false ) );
                databus.send( new EOD( origin ) );
            }
        };

        eventbus.send( new NetworkActivityEvent( FetchBooksAction.class, true ) );

        final Gson gson = new GsonBuilder().create();
        final RestAdapter restadapter = new RestAdapter.Builder()
                .setEndpoint( FetchBooksAction.ENDPOINT )
                .setConverter( new GsonConverter( gson ) )
                .setLogLevel( DBG.verbose ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE )
                .setLog( new AndroidLog( "DBG" ) )
                .build();

        final Subscription restsubscription = restadapter.create( infinitebooks.class )
                .get( "500","false",String.valueOf( start ),String.valueOf( count ) )
                .subscribeOn( Schedulers.newThread() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( observer );
        compositesubscription.add( restsubscription );
    }

    @Override
    public void cancel() {
        eventbus.send( new NetworkActivityEvent( FetchBooksAction.class,false ) );
        databus.send( new EOD( origin ) );

        compositesubscription.unsubscribe();

        DBG.m( "FetchBooksAction.cancel" );

    }
}
