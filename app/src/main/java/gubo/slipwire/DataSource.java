
package gubo.slipwire;

/**
 *
 */
public interface DataSource<D extends Data>
{
    /**
     *
     * @param position
     * @return
     */
    public D getDataFor( int position );

    /**
     *
     * @param position
     * @param count
     */
    public void getReadyFor( int position, int count );
}
