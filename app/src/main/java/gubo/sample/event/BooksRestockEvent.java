
package gubo.sample.event;

import gubo.slipwire.*;

public class BooksRestockEvent extends Event
{
    public BooksRestockEvent( final Object origin ) {
        super( origin );
    }
}
