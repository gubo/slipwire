
package gubo.slipwire;

import java.io.*;
import java.net.*;

/**
 *
 */
public class JETTY
{
    public static final int DEFAULT_PORT = 52505;

    /*
     * http://www.eclipse.org/jetty/
     * http://wiki.eclipse.org/Jetty
     * http://wiki.eclipse.org/Jetty/Tutorial/Embedding_Jetty
     * http://mvnrepository.com/artifact/org.eclipse.jetty
     * http://puregeekjoy.blogspot.com/2011/06/running-embedded-jetty-in-android-app.html
     */

    private static JETTY instance;

    private org.eclipse.jetty.server.Server server;
    private int port;

    /**
     *
     * @param contextbroker
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     */
    public static final void startup( final ContextBroker contextbroker ) throws IllegalArgumentException,IllegalStateException {
        Util.assertMainThread();

        if ( contextbroker == null ) { throw new IllegalArgumentException(); }
        if ( instance != null ) { throw new IllegalStateException(); }

        DBG.m( "JETTY.startup" );

        if ( instance == null ) {
            instance = new JETTY();
            instance._startup( contextbroker );
        }
    }

    /**
     *
     * @return
     */
    public static int getPort() {
        Util.assertMainThread();

        int port = 0;
        if ( instance != null ) {
            port = instance.port;
        }
        return port;
    }

    /**
     *
     */
    public static void shutdown() {
        Util.assertMainThread();

        if ( instance != null ) {
            instance._shutdown();
            instance = null;
        }

        DBG.m( "JETTY.shutdown" );
    }

    private void _startup( final ContextBroker contextbroker ) {
        try {
            port = findPort();
            server = new org.eclipse.jetty.server.Server( port );
            server.setHandler( new JobletHandler( contextbroker ) );
            server.start();
        } catch ( Exception x ) {
            DBG.m( x );
        }
    }

    private static int findPort() {
        int availableport = 0;

        ServerSocket serversocket = null;
        try {
            serversocket = new ServerSocket( JETTY.DEFAULT_PORT );
            availableport = serversocket.getLocalPort();
        } catch ( IOException x ) {
            DBG.v( "JETTY.findPort: failed to get default port" );
        } finally {
            Util.close( serversocket );
        }

        try {
            if ( availableport == 0 ) {
                serversocket = new ServerSocket( 0 );
                availableport = serversocket.getLocalPort();
            }
        } catch ( IOException x ) {
            DBG.m( x );
        } finally {
            Util.close( serversocket );
        }

        return availableport;
    }

    private void _shutdown() {
        try {
            if ( server != null ) {
                server.stop();
            }
        } catch ( Exception x ) {
            DBG.m( x );
        }
        server = null;
    }

    private JETTY() {}
}
