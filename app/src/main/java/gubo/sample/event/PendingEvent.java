
package gubo.sample.event;

import gubo.slipwire.*;

public class PendingEvent extends Event
{
    public final boolean pending;

    public PendingEvent( final Object origin,final boolean pending ) {
        super( origin );
        this.pending = pending;
    }
}
