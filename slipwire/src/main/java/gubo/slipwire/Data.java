
package gubo.slipwire;

/**
 *
 */
public abstract class Data implements Original
{
    protected final Object origin;

    /**
     *
     * @param origin
     */
    public Data( final Object origin ) {
        this.origin = origin;
    }

    @Override public Object getOrigin() { return origin; }
}
