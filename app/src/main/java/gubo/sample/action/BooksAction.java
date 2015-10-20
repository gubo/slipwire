
package gubo.sample.action;

import javax.inject.*;

import android.app.*;
import android.content.*;

import gubo.sample.*;
import gubo.slipwire.*;

/*
 * TODO: is it possible for this class to be misused and lead to a leak of the injected Activity ?
 */
public class BooksAction extends Action
{
    private final Activity activity;

    @Inject
    public BooksAction( final Object origin,final Activity activity ) throws IllegalArgumentException {
        super( origin );
        if ( activity == null ) { throw new IllegalArgumentException(); }
        this.activity = activity;
    }

    @Override
    public void invoke() {
        final Intent intent = new Intent( activity,BooksActivity.class );
        activity.startActivity( intent );
    }

    @Override
    public void cancel() {}
}
