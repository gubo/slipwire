
package gubo.slipwire;

import java.net.*;
import java.util.*;

import android.os.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;

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

    public static void strict( final boolean on ) {
        if ( on ) {
            StrictMode.setThreadPolicy( new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build() );
            StrictMode.setVmPolicy( new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build() );
            DBG.m( "<< STRICT MODE ON >>" );
        }
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

    public static String getIPv4Address() {
        String ipv4address = null;

        try {
            final List<NetworkInterface> networkinterfaces = Collections.list( NetworkInterface.getNetworkInterfaces() );
            for ( final NetworkInterface networkinterface : networkinterfaces ) {
                final List<InetAddress> addresses = Collections.list( networkinterface.getInetAddresses() );
                for ( final InetAddress address : addresses ) {
                    if ( (address == null) || address.isLoopbackAddress() ) { continue; }
                    if ( address instanceof Inet4Address ) {
                        ipv4address = address.getHostAddress().toString();
                        break;
                    }
                }
            }
        } catch ( Exception x ) {
            DBG.m( x );
        }

        return ipv4address;
    }

    public static void assertMainThread() {
        if ( Looper.myLooper() != Looper.getMainLooper() ) {
            throw new RuntimeException();
        }
    }

    private Util() {}
}
