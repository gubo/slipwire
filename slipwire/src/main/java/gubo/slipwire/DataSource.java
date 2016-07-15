
package gubo.slipwire;

/**
 *
 */
public interface DataSource<D extends Data>
{
    /**
     *
     * @param filters
     * @return
     */
    DataSource<D> filter( java.util.Collection<? extends Filter> filters );

    /**
     *
     * @return
     */
    DataSource<D> sort( Sort sort );

    /**
     *
     * @param position
     * @return
     */
    public D getDataFor( int position );

    /**
     *
     * @param start
     * @param count
     */
    public void getReadyFor( int start,int count );

    /**
     *
     */
    public void requestRefresh();
}
