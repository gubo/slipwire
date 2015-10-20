
package gubo.sample.event;

import gubo.slipwire.*;

public class NetworkActivityEvent extends Event
{
    public final boolean active;

    public NetworkActivityEvent( final Object origin,final boolean active ) {
        super( origin );
        this.active = active;
    }
}
