
package gubo.sample.event;

import gubo.slipwire.*;

public class BusyEvent extends Event
{
    public final boolean busy;

    public BusyEvent( final Object origin,final boolean busy ) {
        super( origin );
        this.busy = busy;
    }
}
