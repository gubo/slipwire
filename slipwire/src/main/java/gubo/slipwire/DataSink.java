
package gubo.slipwire;

/**
 *
 */
public interface DataSink<D extends Data>
{
    /**
     *
     * @param itemcount
     */
    public void setItemCount( int itemcount );

    /**
     *
     * @param position
     */
    public void setPosition( int position );
}
