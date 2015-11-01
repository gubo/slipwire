
package gubo.sample.joblet;

import java.util.*;
import javax.inject.*;

import android.app.*;
import android.content.*;

import gubo.slipwire.*;
import gubo.sample.data.*;

/*
 *
 */
class GalleryTest implements Test
{
    private final ContextBroker contextbroker;
    private final Map<String,String> parameters;

    private String result = "...";

    @Inject
    GalleryTest( final ContextBroker contextbroker,final Map<String,String> parameters ) {
        this.contextbroker = contextbroker;
        this.parameters = parameters;
    }

    @Override
    public void run() {
        try {
            final Intent intent = new Intent( Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
            final ComponentName componentname = intent.resolveActivity( contextbroker.getApplicationContext().get().getPackageManager() );
            if ( componentname != null ) {
                final Activity activity = ( Activity ) contextbroker.getActivityContext().get();
                activity.startActivityForResult( intent,GalleryData.myIntention() );
                result = "ok";
            } else {
                result = "not found";
            }
        } catch ( Exception x ) {
            result = x.getClass().getSimpleName();
            DBG.m( x );
        }
    }

    @Override
    public String getResult() { return result; }
}
