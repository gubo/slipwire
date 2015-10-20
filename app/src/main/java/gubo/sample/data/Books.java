
package gubo.sample.data;

import gubo.slipwire.*;

public class Books extends Data
{
    public int start;
    public int count;
    public Book [] books;

    public Books( final Object origin ) {
        super( origin );
    }
}
