
package gubo.sample;

import javax.inject.*;

import android.app.*;

import rx.*;
import rx.functions.*;
import rx.android.schedulers.*;

import gubo.slipwire.*;
import gubo.sample.view.*;
import gubo.sample.event.*;
import gubo.sample.action.*;
import gubo.sample.presenter.*;

/*
 *
 */
public class BooksManager implements Manageable
{
    private final EventBus eventbus;
    private final DataBus databus;

    private Subscription eventsubscription;
    private BooksPresenter bookspresenter;

    @Inject
    BooksManager( final EventBus eventbus,final DataBus databus ) throws IllegalArgumentException {
        if ( (eventbus == null) || (databus == null) ) { throw new IllegalArgumentException(); }

        this.eventbus = eventbus;
        this.databus = databus;
    }

    @Override
    public void manage() {
        DBG.m( "BooksManager.manage" );

        final Action1<Event> EA1 = new Action1<Event>() {
            @Override public void call( final Event event ) { onEvent( event ); }
        };
        eventsubscription = eventbus.toObserverable().subscribeOn( AndroidSchedulers.mainThread() ).subscribe( EA1 );

        bookspresenter = new BooksPresenter( eventbus,databus );
    }

    @Override
    public void bind( final Activity activity ) {
        DBG.m( "BooksManager.bind" );

        bookspresenter.bind( new BooksAdapter( bookspresenter,activity.findViewById( R.id.books ),25,3 ) );
    }

    @Override
    public void unbind() {
        bookspresenter.bind( null );

        DBG.m( "BooksManager.unbind" );
    }

    @Override
    public void unmanage() {
        bookspresenter.release();

        if ( eventsubscription != null ) {
            eventsubscription.unsubscribe();
            eventsubscription = null;
        }

        DBG.m( "BooksManager.unmanage" );
    }

    private void onEvent( final Event event ) {
        if ( event instanceof FetchBooksEvent ) {
            final FetchBooksEvent fetchbooksevent = ( FetchBooksEvent)event;
            new FetchBooksAction( event.getOrigin(),eventbus,databus,fetchbooksevent.start,fetchbooksevent.count ).invoke();
        }
    }
}
