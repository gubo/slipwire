
package gubo.sample.event;

import gubo.slipwire.*;

public class FetchBooksEvent extends Event
{
    public final int start;
    public final int count;

    public FetchBooksEvent( final Object origin,final int start,final int count ) {
        super( origin );

        this.start = start;
        this.count = count;
    }
}
