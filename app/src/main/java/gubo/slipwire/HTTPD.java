
package gubo.slipwire;

import java.util.*;

import com.google.gson.*;

import fi.iki.elonen.*;

/*
 * dependencies:
 * compile 'com.nanohttpd:nanohttpd:2.1.0'
 * compile 'com.nanohttpd:nanohttpd-webserver:2.1.0'
 *
 * @see http://nanohttpd.org/
 * @see https://github.com/NanoHttpd/nanohttpd
 *
 * TODO: build Jetty library ?
 * http://www.eclipse.org/jetty/
 * https://github.com/jetty-project/i-jetty
 * http://stackoverflow.com/questions/16601299/how-to-create-a-library-project-in-android-studio-and-an-application-project-tha
 *
 */
class HTTPD extends NanoHTTPD
{
    static class Response
    {
        public int ms;
        public Object resultant;
    }

    private JobletFactory jobletfactory;

    HTTPD( final int port ) throws java.io.IOException {
        super( port );
    }

    void setJobletFactory( final JobletFactory jobletfactory ) {
        this.jobletfactory = jobletfactory;
    }

    @Override
    public NanoHTTPD.Response serve( IHTTPSession session ) {
        DBG.v( "Server.serve[" + Thread.currentThread().getId() + "]" );

        final Method method = session.getMethod();
        final String uri = session.getUri();
        final Map<String,String> parameters = session.getParms();

        final HTTPD.Response serverresponse = new HTTPD.Response();

        JOBLET: {
            final Joblet joblet = jobletfactory.newJoblet( method.name(),uri,parameters );
            final java.io.Serializable resultant = joblet.perform();
            serverresponse.resultant = resultant;
        }

        DELAY: {
            final String _delay = parameters.get( "delay" );
            delay( _delay );
        }

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final String json = gson.toJson( serverresponse, HTTPD.Response.class );

        final NanoHTTPD.Response nanoresponse = new NanoHTTPD.Response( json );
        nanoresponse.setMimeType( "application/json" );
        nanoresponse.setStatus( NanoHTTPD.Response.Status.OK );
        return nanoresponse;
    }

    private void delay( final String _delay ) {
        int delay = 0;
        try {
            if ( _delay != null ) {
                delay = Integer.parseInt( _delay );
                if ( delay > 0 ) { Thread.sleep( delay ); }
            }
        } catch ( Exception x ) {
            DBG.m( x );
        }
    }
}
