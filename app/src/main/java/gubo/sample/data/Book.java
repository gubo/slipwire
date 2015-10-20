
package gubo.sample.data;

import gubo.slipwire.*;

public class Book extends Data
{
    public String id;
    public int index;
    public String title;
    public String thumbnailurl;

    public Book( final Object origin ) {
        super( origin );
    }
}
