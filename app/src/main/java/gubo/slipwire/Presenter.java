
package gubo.slipwire;

/**
 *
 */
public interface Presenter
{
    /**
     *
     */
    public static interface Display {}

    /**
     *
     * @param display
     * @param <D>
     */
    public <D extends Display> void bind( D display );

    /**
     *
     */
    public void release();
}
