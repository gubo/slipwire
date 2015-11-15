
package gubo.slipwire;

/**
 *
 */
public abstract class Action implements Original,Cancelable
{
    protected final Object origin;

    /**
     *
     * @param origin
     */
    public Action( final Object origin ) {
        this.origin = origin;
    }

    @Override public Object getOrigin() { return origin; }

    /**
     *
     */
    public abstract void invoke();
}
