
package gubo.sample.presenter;

import rx.*;
import rx.functions.*;
import rx.android.schedulers.*;

import gubo.slipwire.*;
import gubo.sample.data.*;
import gubo.sample.event.*;
import rx.subscriptions.CompositeSubscription;

/*
 *
 */
public class BooksPresenter implements Presenter,DataSource<Book>
{
    public interface Display extends Presenter.Display,DataSink
    {
        public void release();
    }

    private final IndexCache<Book> bookbackingstorecache = new IndexCache<Book>( 250,.85F );

    private final EventBus eventbus;
    private final DataBus databus;

    private BooksPresenter.Display display;
    private Subscription eventsubscription;
    private Subscription datasubscription;
    private int currentposition;
    private int itemcount;

    public BooksPresenter( final EventBus eventbus,final DataBus databus ) throws IllegalArgumentException {
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

        itemcount = 0;
        currentposition = 0;
    }

    @Override
    public <D extends Presenter.Display> void bind( D display ) {
        DBG.v( "BooksPresenter.bind " + display );

        if ( this.display != null ) {
            this.display.release();
        }

        this.display = ( BooksPresenter.Display)display;

        refresh();
    }

    @Override
    public void release() {
        if ( datasubscription != null ) {
            datasubscription.unsubscribe();
            datasubscription = null;
        }

        if ( eventsubscription != null ) {
            eventsubscription.unsubscribe();
            eventsubscription = null;
        }

        if ( display != null ) {
            display.release();
        }

        DBG.m( "BooksPresenter.release" );
    }

    @Override
    public Book getDataFor( final int position ) {
        final Book book = bookbackingstorecache.get( position );
        return book;
    }

    @Override
    public void getReadyFor( final int position,final int count ) {
        currentposition = position;
        int _start = ( count >= 0 ? position : (position + count) );
        int _count = Math.abs( count );
        if ( _start < 0 ) { _start = 0; }
        fetch( _start,_count );
    }

    private void onEvent( final Event event ) {
        if ( event instanceof BooksRestockEvent ) {
            if ( (itemcount == 0) && (currentposition == 0) ) {
                getReadyFor( 0,50 );
            }
        }
    }

    private void onData( final Data data ) {
        if ( data instanceof Books ) {
            final Books books = ( Books)data;
            for ( final Book book : books.books ) {
                bookbackingstorecache.put( book.index,book );
            }
            final int extent = ( books.start + books.count );
            itemcount = Math.max( itemcount,extent );
            if ( display != null ) {
                display.setItemCount( itemcount );
            }
        }
    }

    private void fetch( final int start,final int count ) {
        eventbus.send( new FetchBooksEvent( BooksPresenter.class,start,count ) );
    }

    private void refresh() {
        if ( display != null ) {
            display.setItemCount( itemcount );
            display.setPosition( currentposition );
        }
    }
}
