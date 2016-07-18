
package gubo.slipwire;

import android.support.v7.widget.*;

import rx.*;

/**
 * Created by JEFF on 7/17/2016.
 */
public class ReactiveLinearScrollListener extends RecyclerView.OnScrollListener
{
    private final LinearLayoutManager linearlayoutmanager;
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

    public ReactiveLinearScrollListener( final LinearLayoutManager linearlayoutmanager,final Subscriber<? super Page> subscriber,final int pagesize,final int lookahead ) {
        this.linearlayoutmanager = linearlayoutmanager;
        this.subscriber = subscriber;
        this.orientation = linearlayoutmanager.getOrientation();
        this.pagesize = pagesize;
        this.lookahead = lookahead;
    }

    @Override
    public void onScrolled( final RecyclerView recyclerView, final int dx,final int dy ) {
        final int direction = normalize( orientation == LinearLayoutManager.HORIZONTAL ? dx : dy );
        switch ( direction ) {
        case -1:
            final int firstvisibleposition = linearlayoutmanager.findFirstVisibleItemPosition();
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
            final int lastvisibleposition = linearlayoutmanager.findLastVisibleItemPosition();
            if ( (lastvisibleposition > 0) && (lastvisibleposition != previous.lastvisibleposition) ) {
                final int lastitemposition = ( linearlayoutmanager.getItemCount() - 1 );
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
