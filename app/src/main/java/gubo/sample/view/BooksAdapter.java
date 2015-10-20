
package gubo.sample.view;

import java.util.concurrent.*;

import android.view.*;
import android.support.v7.widget.*;

import rx.*;
import rx.android.schedulers.*;

import gubo.slipwire.*;
import gubo.sample.data.*;
import gubo.sample.presenter.*;

/*
 *
 */
public class BooksAdapter implements BooksPresenter.Display
{
    static class BooksAdapterOnScrollListener extends RecyclerView.OnScrollListener
    {
        private final Subscriber<? super Pagination> subscriber;
        private final LinearLayoutManager linearlayoutmanager;
        private final Previous previous = new Previous();
        private final int pagesize;
        private final int lookahead;

        static class Previous
        {
            int firstvisibleposition = -1;
            int lastvisibleposition = -1;
        }

        BooksAdapterOnScrollListener( final LinearLayoutManager lineaerlayoutmanager,final Subscriber<? super Pagination> subscriber,final int pagesize,final int lookahead ) {
            this.linearlayoutmanager = lineaerlayoutmanager;
            this.subscriber = subscriber;
            this.pagesize = pagesize;
            this.lookahead = lookahead;
        }

        @Override
        public void onScrolled( final RecyclerView recyclerView, final int dx,final int dy ) {
            int direction = 0;
            switch ( linearlayoutmanager.getOrientation() ) {
            case LinearLayoutManager.HORIZONTAL:
                if ( dx > 0 ) {
                    direction = +1;
                } else if ( dx < 0 ) {
                    direction = -1;
                }
                break;
            case LinearLayoutManager.VERTICAL:
                if ( dy > 0 ) {
                    direction = +1;
                } else if ( dy < 0 ) {
                    direction = -1;
                }
                break;
            }

            switch ( direction ) {
            case -1:
                BACKWARD: {
                    final int firstvisibleposition = linearlayoutmanager.findFirstVisibleItemPosition();
                    if ( (firstvisibleposition > 0) && (firstvisibleposition != previous.firstvisibleposition) ) {
                        final boolean boundary = ( (firstvisibleposition % pagesize) == 0 );
                        if ( boundary ) {
                            final Pagination pagination = new Pagination( firstvisibleposition,-pagesize );
                            subscriber.onNext( pagination );
                        }
                    }
                    previous.firstvisibleposition = firstvisibleposition;
                }
                break;
            case +1:
                FORWARD:
                {
                    final int lastvisibleposition = linearlayoutmanager.findLastVisibleItemPosition();
                    if ( (lastvisibleposition > 0) && (lastvisibleposition != previous.lastvisibleposition) ) {
                        final int lastitemposition = ( linearlayoutmanager.getItemCount() - 1 );
                        final boolean boundary = ( (lastvisibleposition % pagesize) == 0 );
                        final boolean end = ( lastvisibleposition == lastitemposition );
                        if ( boundary || end ) {
                            final Pagination pagination = new Pagination( lastvisibleposition,(pagesize * lookahead) );
                            subscriber.onNext( pagination );
                        }
                    }
                    previous.lastvisibleposition = lastvisibleposition;
                }
                break;
            }
        }
    };

    private final DataSource<Book> datasource;
    private final View view;
    private final int pagesize;
    private final int lookahead;

    private RecyclerView.LayoutManager recyclerlayoutmanager;
    private BooksRecyclerViewAdapter recycleradapter;
    private RecyclerView recyclerview;
    private Subscription subscription;

    public BooksAdapter( final DataSource<Book> datasource,final View view,final int pagesize,final int lookahead ) throws IllegalArgumentException {
        if ( (datasource == null) || (view == null) ) { throw new IllegalArgumentException(); }

        this.datasource = datasource;
        this.view = view;
        this.pagesize = pagesize;
        this.lookahead = lookahead;

        recyclerview = ( RecyclerView)view.findViewById( R.id.books_recyclerview );
        recyclerview.setHasFixedSize( true );

        recyclerlayoutmanager = new LinearLayoutManager( recyclerview.getContext() );
        recyclerview.setLayoutManager( recyclerlayoutmanager );

        recycleradapter = new BooksRecyclerViewAdapter( datasource,pagesize );
        recyclerview.setAdapter( recycleradapter );

        /*
         * @see https://gist.github.com/dustin-graham/52eaaab1cb3a41aba444
         */
        final Observable.OnSubscribe<Pagination> observable = new Observable.OnSubscribe<Pagination>()
        {
            @Override
            public void call( final Subscriber<? super Pagination> subscriber ) {
                final BooksAdapterOnScrollListener scrollistener = new BooksAdapterOnScrollListener( ( LinearLayoutManager)recyclerlayoutmanager,subscriber,pagesize,lookahead );
                recyclerview.setOnScrollListener( scrollistener );
            }
        };
        final Observable<Pagination> pageobservable = Observable.create( observable ).debounce( 400, TimeUnit.MILLISECONDS );
        final Observer<Pagination> pageobserver = new Observer<Pagination>() {
            @Override public void onCompleted() { DBG.m( "pageobserver.onCompleted" ); }
            @Override public void onError( Throwable x ) { x.printStackTrace(); }
            @Override public void onNext( final Pagination pagination ) { page( pagination ); }
        };
        subscription = pageobservable.observeOn( AndroidSchedulers.mainThread() ).subscribe( pageobserver );
    }

    @Override
    public void release() {
        if ( subscription != null ) {
            subscription.unsubscribe();
            subscription = null;
        }
        DBG.m( "BooksAdapter.release" );
    }

    @Override
    public void setItemCount( final int itemcount ) {
        recycleradapter.setItemCount( itemcount );
        recycleradapter.notifyDataSetChanged();
    }

    @Override
    public void setPosition( final int position ) {
        recyclerview.scrollToPosition( position );
    }

    private void page( final Pagination pagination ) {
        recycleradapter.page( pagination );
    }
}
