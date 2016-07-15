
package gubo.slipwire;

import java.io.*;
import java.net.*;
import java.math.*;
import java.util.*;
import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.*;

import android.os.*;
import android.app.*;
import android.net.*;
import android.view.*;
import android.util.*;
import android.content.*;
import android.content.pm.*;
import android.view.inputmethod.*;

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
    public static String getApplicationName( final Context context ) {
        String name = null;
        try {
            final PackageManager packagemanager = context.getPackageManager();
            final PackageInfo packageinfo = packagemanager.getPackageInfo( context.getPackageName(),0 );
            name = packageinfo.applicationInfo.loadLabel( packagemanager ).toString() + "(" + packageinfo.packageName + ")";
        } catch ( Exception x ) {
            DBG.m( x );
        }
        return name;
    }

    /**
     *
     * @param context
     * @return
     */
    public static String version( final Context context ) {
        String version = null;
        try {
            final PackageInfo packageinfo = context.getPackageManager().getPackageInfo( context.getPackageName(),0 );
            version = packageinfo.versionName;
        } catch ( Exception x ) {
            DBG.m( x );
        }
        return version;
    }

    /**
     *
     * @param context
     */
    public static void showDiagnosticInfo( final Context context ) {
        try {
            DBG.m( "  Build.VERSION.SDK_INT: " + Build.VERSION.SDK_INT );
            DBG.m( "  Build.VERSION.CODENAME: " + Build.VERSION.CODENAME );
            DBG.m( "  Build.DEVICE: " + android.os.Build.DEVICE );
            DBG.m( "  Build.MODEL: " + android.os.Build.MODEL );
            DBG.m( "  Build.PRODUCT: " + android.os.Build.PRODUCT );

            final DisplayMetrics displaymetrics = new DisplayMetrics();
            final WindowManager windowmanager = ( WindowManager)context.getSystemService( Context.WINDOW_SERVICE );
            windowmanager.getDefaultDisplay().getMetrics( displaymetrics );
            DBG.m( "  DisplayMetrics.density: " + displaymetrics.density );
            DBG.m( "  DisplayMetrics.densityDpi: " + displaymetrics.densityDpi );
            DBG.m( "  DisplayMetrics.heightPixels: " + displaymetrics.heightPixels );
            DBG.m( "  DisplayMetrics.widthPixels: " + displaymetrics.widthPixels );
            DBG.m( "  DisplayMetrics.scaledDensity: " + displaymetrics.scaledDensity );
            DBG.m( "  DisplayMetrics.xdpi: " + displaymetrics.xdpi );
            DBG.m( "  DisplayMetrics.ydpi: " + displaymetrics.ydpi );
        } catch ( Exception x ) {
            DBG.m( x );
        }
    }

    /**
     *
     */
    public static void strict() {
        StrictMode.setThreadPolicy( new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().penaltyFlashScreen().build() );
        StrictMode.setVmPolicy( new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build() );
        DBG.w( "STRICT MODE ON" );
    }

    /**
     *
     * @param closeable
     */
    public static void close( final java.io.Closeable closeable ) {
        try {
            if ( closeable != null ) {
                closeable.close();
            }
        } catch ( java.io.IOException x ) {
            DBG.m( x );
        }
    }

    /**
     *
     * @param context
     * @param classname
     * @return
     */
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

    /**
     *
     * @return
     */
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

    /**
     * http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
     * http://developer.android.com/training/basics/network-ops/managing.html
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable( final Context context,final boolean mobile ) {
        boolean available = false;
        try {
            final ConnectivityManager connectivitymanager = ( ConnectivityManager)context.getSystemService( Context.CONNECTIVITY_SERVICE );
            final NetworkInfo networkinfo = connectivitymanager.getActiveNetworkInfo();
            if ( (networkinfo != null) && networkinfo.isConnectedOrConnecting() ) {
                switch ( networkinfo.getType() ) {
                case ConnectivityManager.TYPE_WIFI:
                case ConnectivityManager.TYPE_WIMAX:
                case ConnectivityManager.TYPE_ETHERNET:
                    available = true;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    if ( mobile ) { available = true; }
                    break;
                }
            }
        } catch ( Throwable x ) {
            DBG.m( x );
        }
        return available;
    }

    /**
     *
     */
    public static void assertMainThread() {
        if ( Looper.myLooper() != Looper.getMainLooper() ) {
            throw new RuntimeException();
        }
    }

    /**
     *
     * @param string
     * @return
     */
    public static boolean isBlank( final String string ) {
        boolean blank = true;
        if ( string != null ) {
            blank = ( string.length() < 1 );
        }
        return blank;
    }

    /**
     * Minimum:
     * a@a.a
     *
     * @param email
     * @return
     */
    public boolean isValidEmail( final String email ) {
        boolean validEmail = false;
        if ( email != null ) {
            final int atIndex = email.indexOf( '@' );
            final int lastDotIndex = email.lastIndexOf( '.' );
            if ( (atIndex > 0) && (lastDotIndex > (atIndex+1)) ) {
                validEmail = ( email.length() > 5 );
            }
        }
        return validEmail;
    }

    /**
     *
     * @param view
     */
    public static void showSoftKeyboard( final View view ) {
        if ( view == null ) { return; }

        final Runnable action = new Runnable() {
            @Override
            public void run() {
                final InputMethodManager imm = ( InputMethodManager )view.getContext().getSystemService( Context.INPUT_METHOD_SERVICE );
                imm.showSoftInput( view,InputMethodManager.SHOW_IMPLICIT );
            }
        };

        final View.OnFocusChangeListener restoreOnFocusChangeListener = view.getOnFocusChangeListener();

        final View.OnFocusChangeListener temporaryOnFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange( final View v,final boolean hasFocus ) {
                view.setOnFocusChangeListener( restoreOnFocusChangeListener );
                view.postDelayed( action,25 );
            }
        };

        view.setOnFocusChangeListener( temporaryOnFocusChangeListener );
        view.requestFocus();
    }

    /**
     *
     * @param view
     */
    public static void hideSoftKeyboard( final View view ) {
        if ( view == null ) { return; }

        final Runnable action = new Runnable() {
            @Override
            public void run() {
                final InputMethodManager imm = ( InputMethodManager )view.getContext().getSystemService( Context.INPUT_METHOD_SERVICE );
                imm.hideSoftInputFromWindow( view.getWindowToken(),InputMethodManager.HIDE_IMPLICIT_ONLY );
            }
        };

        view.postDelayed( action,25 );
    }

    /**
     *
     * @param s
     * @return
     */
    public static String mask( final String s ) {
        final StringBuffer buffer = new StringBuffer( 25 );
        final int count = ( s != null ? s.length() : 0 );
        for ( int n=0; n < count; n++ ) { buffer.append( '*' ); }
        return buffer.toString();
    }

    /**
     *
     * @param s
     * @return
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public static String encrypt( final String s ) throws GeneralSecurityException,UnsupportedEncodingException {
        String encrypted = null;

        try {
            if ( s != null ) {
                final SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance( "PBEWithMD5AndDES" );
                final SecretKey secretKey = secretKeyFactory.generateSecret( new PBEKeySpec( secret.toCharArray() ) );
                final Cipher cipher = Cipher.getInstance( "PBEWithMD5AndDES" );
                cipher.init( Cipher.ENCRYPT_MODE,secretKey,new PBEParameterSpec( SALT,20 ) );
                final byte [] stringBytes = s.getBytes( "UTF-8" );
                final byte [] encryptedBytes = cipher.doFinal( stringBytes );
                final byte [] encodedBytes = Base64.encode( encryptedBytes,Base64.DEFAULT );
                encrypted = new String( encodedBytes,"UTF-8" );
            }
        } catch ( GeneralSecurityException x ) {
            throw x;
        } catch ( UnsupportedEncodingException x ) {
            throw x;
        } catch ( Exception x ) {
            DBG.m( x );
        }

        return encrypted;
    }

    /**
     *
     * @param s
     * @return
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public static String decrypt( final String s ) throws GeneralSecurityException,UnsupportedEncodingException {
        String decrypted = null;

        try {
            if ( s != null ) {
                final SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance( "PBEWithMD5AndDES" );
                final SecretKey secretKey = secretKeyFactory.generateSecret( new PBEKeySpec( secret.toCharArray() ) );
                final Cipher cipher = Cipher.getInstance( "PBEWithMD5AndDES" );
                cipher.init( Cipher.DECRYPT_MODE,secretKey,new PBEParameterSpec( SALT,20 ) );
                final byte [] stringBytes = s.getBytes( "UTF-8" );
                final byte [] decodedBytes = Base64.decode( stringBytes,Base64.DEFAULT );
                final byte [] decryptedBytes = cipher.doFinal( decodedBytes );
                decrypted = new String( decryptedBytes,"UTF-8" );
            }
        } catch ( GeneralSecurityException x ) {
            throw x;
        } catch ( UnsupportedEncodingException x ) {
            throw x;
        } catch ( Exception x ) {
            DBG.m( x );
        }

        return decrypted;
    }

    /*
     * http://android-developers.blogspot.com/2013/02/using-cryptography-to-store-credentials.html
     */
    static
    {
        try {
            final SecureRandom secureRandom = new SecureRandom(); // Do not seed secureRandom ! Automatically seeded from system entropy.
            secret = new BigInteger( 130,secureRandom ).toString( 32 );
        } catch ( Exception x ) {
            DBG.m( x );
        }
    }

    private static final byte[] SALT = { (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12, (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12 };
    private static String secret;

    private Util() {}

    public static void main( final String [] args ) {
        try {
            final String encrypted = Util.encrypt( "fred flintstone" );
            final String decrypted = Util.decrypt( encrypted );
            DBG.m( encrypted + " / " + decrypted );
        } catch ( Exception x ) {
            DBG.m( x );
        }
    }

    public static void test() {
        Util.main( new String [] {} );
    }
}
