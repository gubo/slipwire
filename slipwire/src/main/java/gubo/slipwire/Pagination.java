
package gubo.slipwire;

/**
 *
 */
public class Pagination
{
    /**
     *
     */
    public final int start;

    /**
     *
     */
    public final int count;

    /**
     *
     * @param start
     * @param count
     */
    public Pagination( final int start, final int count ) {
        this.start = start;
        this.count = count;
    }
}
