
package gubo.slipwire;

/**
 *
 */
public interface Action extends Original,Cancelable
{
    /**
     *
     */
    public abstract void invoke();
}
