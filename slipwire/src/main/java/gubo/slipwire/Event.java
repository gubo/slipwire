
package gubo.slipwire;

/**
 *
 */
public abstract class Event implements Original
{
    protected final Object origin;

    /**
     *
     * @param origin
     */
    public Event( final Object origin ) {
        this.origin = origin;
    }

    @Override public Object getOrigin() { return origin; }
}
