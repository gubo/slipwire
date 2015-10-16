
package gubo.slipwire;

/**
 *
 */
public class Latch
{
    private boolean tripped;

    /**
     *
     */
    public Latch() {
        tripped = false;
    }

    /**
     *
     */
    public synchronized boolean trip() {
        boolean _tripped = !tripped;
        tripped = true;
        return _tripped;
    }
}
