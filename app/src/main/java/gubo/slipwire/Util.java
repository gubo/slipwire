
package gubo.slipwire;

import android.os.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;

import java.util.List;

/**
 *
 */
public class Util
{
    /**
     *
     * @param context
     * @return
     */
    public static String version( final Context context ) {
        String version = null;
        try {
            PackageInfo packageinfo = context.getPackageManager().getPackageInfo( context.getPackageName(),0 );
            version = packageinfo.versionName;
        } catch ( Exception x ) {
            DBG.m( x );
        }
        return version;
    }

    public static void close( final java.io.Closeable closeable ) {
        try {
            if ( closeable != null ) {
                closeable.close();
            }
        } catch ( java.io.IOException x ) {
            DBG.m( x );
        }
    }

    public static boolean isServiceRunning( final Context context,final String classname ) {
        boolean running = false;
        try {
            final ActivityManager activitymanager = ( ActivityManager)context.getSystemService( Context.ACTIVITY_SERVICE );
            final List<ActivityManager.RunningServiceInfo> runningserviceinfos = activitymanager.getRunningServices( Integer.MAX_VALUE );
            for ( final ActivityManager.RunningServiceInfo runningserviceinfo : runningserviceinfos ) {
                final String serviceclassname = runningserviceinfo.service.getClassName();
                if ( serviceclassname == null ) { continue; }
                if ( serviceclassname.equals( classname ) ) {
                    running = true;
                    break;
                }
            }
        } catch ( Exception x  ) {
            DBG.m( x );
        }
        return running;
    }

    public static void assertMainThread() {
        if ( Looper.myLooper() != Looper.getMainLooper() ) {
            throw new RuntimeException();
        }
    }

    private Util() {}
}
