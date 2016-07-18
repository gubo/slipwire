
package gubo.slipwire;

import android.support.v7.widget.*;

import rx.*;

/**
 * Created by JEFF on 7/17/2016.
 */
public class ReactiveStaggeredScrollListener extends RecyclerView.OnScrollListener
{
    private final StaggeredGridLayoutManager staggeredgridlayoutmanager;
    private final Subscriber<? super Page> subscriber;
    private final Previous previous = new Previous();
    private final int orientation;
    private final int pagesize;
    private final int lookahead;

    static class Previous
    {
        int firstvisibleposition = -1;
        int lastvisibleposition = -1;
    }

    public ReactiveStaggeredScrollListener( final StaggeredGridLayoutManager staggeredgridlayoutmanager,final Subscriber<? super Page> subscriber,final int pagesize,final int lookahead ) {
        this.staggeredgridlayoutmanager = staggeredgridlayoutmanager;
        this.subscriber = subscriber;
        this.orientation = staggeredgridlayoutmanager.getOrientation();
        this.pagesize = pagesize;
        this.lookahead = lookahead;
    }

    @Override
    public void onScrolled( final RecyclerView recyclerView, final int dx,final int dy ) {
        final int direction = normalize( orientation == LinearLayoutManager.HORIZONTAL ? dx : dy );
        switch ( direction ) {
        case -1:
            final int [] firsts = staggeredgridlayoutmanager.findFirstVisibleItemPositions( null );
            final int firstvisibleposition = ( (firsts != null) && (firsts.length > 1) ? firsts[ 0 ] : 0 );
            if ( (firstvisibleposition > 0) && (firstvisibleposition != previous.firstvisibleposition) ) {
                final boolean boundary = ( (firstvisibleposition % pagesize) == 0 );
                if ( boundary ) {
                    final int count = (pagesize * lookahead);
                    final int start = Math.max( firstvisibleposition-count,0 );
                    subscriber.onNext( new Page( start,count ) );
                }
            }
            previous.firstvisibleposition = firstvisibleposition;
            break;
        case +1:
            final int [] lasts = staggeredgridlayoutmanager.findLastVisibleItemPositions( null );
            final int lastvisibleposition = ( (lasts != null) && (lasts.length > 1) ? lasts[ 0 ] : 0 );
            if ( (lastvisibleposition > 0) && (lastvisibleposition != previous.lastvisibleposition) ) {
                final int lastitemposition = ( staggeredgridlayoutmanager.getItemCount() - 1 );
                final boolean boundary = ( (lastvisibleposition % pagesize) == 0 );
                final boolean end = ( lastvisibleposition == lastitemposition );
                if ( boundary || end ) {
                    final int start = lastvisibleposition;
                    final int count = (pagesize * lookahead);
                    subscriber.onNext( new Page( start,count ) );
                }
            }
            previous.lastvisibleposition = lastvisibleposition;
            break;
        }
    }

    private static int normalize( final int n ) {
        int normalized = ( n < 0 ? -1 : +1 );
        return normalized;
    }
}
