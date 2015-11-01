
package gubo.slipwire;

import java.util.*;

/**
 * NOT THREAD-SAFE
 */
public class IndexCache<E>
{
    private final Map<Integer,E> map = new LinkedHashMap<>();
    private final int maxentries;
    private final float tolerance;

    /**
     *
     * @param maxentries
     * @param tolerance
     * @throws IllegalArgumentException
     */
    public IndexCache( final int maxentries, final float tolerance ) throws IllegalArgumentException {
        if ( maxentries < 1 ) { throw new IllegalArgumentException(); }
        if ( tolerance < 0F ) { throw new IllegalArgumentException(); }
        this.maxentries = maxentries;
        this.tolerance = tolerance;
    }

    /**
     *
     * @param position
     * @param entry
     */
    public void put( final Integer position, final E entry ) {
        if ( position == null ) { return; }
        map.put( position, entry );
        flush( position );
    }

    /**
     *
     * @param position
     * @return
     */
    public E get( final Integer position ) {
        E entry = null;
        if ( position != null ) {
            entry = map.get( position );
        }
        return entry;
    }

    /*
     * TODO: single iteration over keys ? make O(n)
     */
    void flush( final Integer position ) {
        if ( position == null ) { return; }
        if ( map.size() < maxentries ) { return; }

        final long anchorposition = position.longValue();
        Integer farthest = null;

        FINDFARTHEST:
        {
            long maxdistance = 0;
            for ( final Integer nextposition : map.keySet() ) {
                final long distance = Math.abs( nextposition.longValue() - anchorposition );
                if ( distance > maxdistance ) {
                    maxdistance = distance;
                    farthest = nextposition;
                }
            }
        }

        REMOVEALLWITHINPERCENTAGEOFFARTHEST:
        {
            final Set<Integer> removals = new HashSet<>( 50 );
            if ( farthest != null ) {
                map.remove( farthest );
                final long farthestdistance = Math.abs( farthest.longValue() - anchorposition );
                final float cutoffdistance = (farthestdistance * tolerance);
                for ( final Integer nextposition : map.keySet() ) {
                    final long distance = Math.abs( nextposition.longValue() - anchorposition );
                    if ( distance > cutoffdistance ) {
                        removals.add( nextposition );
                    }
                }
            }
            for ( final Integer nextposition : removals ) {
                if ( nextposition == null ) { continue; }
                map.remove( nextposition );
            }
        }
    }
}
