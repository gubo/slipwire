
package gubo.slipwire;

/**
 * Created by JEFF on 5/9/2016.
 */
public class Stopwatch
{
    private long startms;
    private long stopms;

    public Stopwatch start() {
        startms = System.currentTimeMillis();
        stopms = 0;
        return this;
    }

    public long time() {
        if ( stopms > 0 ) { return stopms; }
        final long ms = ( System.currentTimeMillis() - startms );
        return ms;
    }

    public long interval() {
        if ( stopms > 0 ) { return ( stopms - startms ); }
        final long now = System.currentTimeMillis();
        final long ms = ( now - startms );
        startms = now;
        return ms;
    }

    public long stop() {
        final long ms = ( System.currentTimeMillis() - startms );
        startms = 0;
        stopms = ms;
        return ms;
    }
}
