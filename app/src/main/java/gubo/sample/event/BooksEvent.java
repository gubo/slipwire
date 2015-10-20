
package gubo.sample.event;

import android.app.*;

import gubo.slipwire.*;

public class BooksEvent extends Event
{
    public final Activity activity;

    public BooksEvent( final Object origin,final Activity activity ) {
        super( origin );
        this.activity = activity;
    }
}
