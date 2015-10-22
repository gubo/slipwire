
package gubo.slipwire;

import java.io.*;
import java.net.*;

import android.os.*;
import android.app.*;
import android.content.*;

/**
 *
 */
public class Server extends Service
{
    /**
     *
     */
    public class Binder extends android.os.Binder
    {
        /**
         *
         * @return
         */
        public Server getService() { return Server.this; }
    }

    private static final int DEFAULT_PORT   = 55808;
    private static final int UNAVAILABLE    = -1;

    private final IBinder binder = new Server.Binder();

    private JobletFactory jobletfactory;
    private HTTPD httpd;
    private int port;

    @Override
    public void onCreate() {
        DBG.m( "Server.onCreate" );
        start();
    }

    @Override
    public int onStartCommand( final Intent intent,final int flags,final int startId ) {
        DBG.m( "Server.onStartCommand" );
        try {
            final String classname = intent.getStringExtra( "JobletFactory" );
            if ( classname != null ) {
                jobletfactory = ( JobletFactory ) Class.forName( classname ).newInstance();
                httpd.setJobletFactory( jobletfactory );
            }
        } catch ( Exception x ) {
            DBG.m( x );
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind( final Intent intent ) { return binder; }

    /**
     *
     * @return
     */
    public int getPort() { return port; }

    @Override
    public void onDestroy() {
        stop();
        DBG.m( "Server.onDestroy" );
    }

    private void start() {
        try {
            port = Server.getAvailablePort();
            if ( httpd == null ) {
                httpd = new HTTPD( port );
                httpd.start();
                DBG.m( "Server.start:"+port );
            }
        } catch ( Exception x ) {
            DBG.m( x );
        }
    }

    private static int getAvailablePort() {
        int _port = UNAVAILABLE;

        DEFAULTPORT : {
            ServerSocket serversocket = null;
            try {
                serversocket = new ServerSocket( Server.DEFAULT_PORT );
                _port = serversocket.getLocalPort();
            } catch ( IOException x ) {
                _port = UNAVAILABLE;
                DBG.w( "FAILED TO GET DEFAULT_PORT:" + _port );
            } finally {
                Util.close( serversocket );
            }
        }

        AVAILABLEPORT: {
            ServerSocket serversocket = null;
            try {
                if ( _port == UNAVAILABLE ) {
                    serversocket = new ServerSocket( 0 );
                    _port = serversocket.getLocalPort();
                }
            } catch ( IOException x ) {
                DBG.w( "FAILED TO GET DEFAULT_PORT:" + _port );
            } finally {
                Util.close( serversocket );
            }
        }

        return _port;
    }

    private void stop() {
        try {
            if ( httpd != null ) {
                DBG.m( "Server.stop" );
                httpd.stop();
                httpd = null;
            }
        } catch ( Exception x ) {
            DBG.m( x );
        }
    }
}
