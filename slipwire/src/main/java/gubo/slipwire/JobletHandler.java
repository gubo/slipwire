
package gubo.slipwire;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.xmlpull.v1.*;
import android.content.*;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.*;

/*
 * http://www.eclipse.org/jetty/
 * http://wiki.eclipse.org/Jetty
 * http://wiki.eclipse.org/Jetty/Tutorial/Embedding_Jetty
 * http://mvnrepository.com/artifact/org.eclipse.jetty
 * http://puregeekjoy.blogspot.com/2011/06/running-embedded-jetty-in-android-app.html
 */
class JobletHandler extends AbstractHandler
{
    static class JobletContext
    {
        String joblet_name;
        String joblet_class;
        String url_pattern;

        public String toString() { return "JC<" + joblet_name + "," + joblet_class + "," + url_pattern + ">"; }
    }

    private final Map<String,JobletContext> map = new HashMap<>();
    private final ContextBroker contextbroker;

    JobletHandler( final ContextBroker contextbroker ) {
        this.contextbroker = contextbroker;
        readxml();
    }

    @Override
    public void handle( final String target,final Request baserequest,final HttpServletRequest request,final HttpServletResponse response ) throws IOException,ServletException {
        String json = "{}";

        JobletContext jobletcontext = null;
        for ( final JobletContext _jobletcontext : map.values() ) {
            if ( _jobletcontext.url_pattern.equals( target ) ) {
                jobletcontext = _jobletcontext;
                break;
            }
        }

        try {
//            if ( jobletcontext != null ) {
//                final Joblet joblet = ( Joblet)Class.forName( jobletcontext.joblet_class ).newInstance();
//                final Map requestparameters = request.getParameterMap();
//                final Map<String,Object> jobletparameters = new HashMap<>();
//                for ( final Object o : requestparameters.keySet() ) {
//                    if ( o instanceof String ) {
//                        final String name = ( String)o;
//                        final Object value = requestparameters.get( o );
//                        if ( value instanceof String [] ) {
//                            final String [] values = ( String [])value;
//                            if ( values.length > 0 ) {
//                                jobletparameters.put( name,values[ 0 ] );
//                            }
//                        }
//                    }
//                }
//                jobletparameters.put( "android.content.Context",context );
//                json = joblet.perform( jobletparameters );
//            }

            final Map requestparameters = request.getParameterMap();
            final Map<String,String> jobletparameters = new HashMap<>();
            for ( final Object _key : requestparameters.keySet() ) {
                final Object _value = requestparameters.get( _key );
                if ( (_key instanceof String) && (_value instanceof String) ) {
                    jobletparameters.put( ( String)_key,( String)_value );
                }
            }

            final Joblet joblet = ( Joblet)Class.forName( jobletcontext.joblet_class ).newInstance();
            joblet.setContextBroker( contextbroker );
            joblet.setParameters( jobletparameters );
            json = joblet.perform();
        } catch ( Exception x ) {
            DBG.m( x );
        }

        response.setContentType( "application/json" );
        response.setStatus( 200 );
        baserequest.setHandled( true );
        response.getWriter().println( json );
    }

    private void readxml() {
        InputStream input = null;
        try {
            final Context context = contextbroker.getApplicationContextReference().get();
            input = ( context != null ? context.getAssets().open( "job.xml" ) : new FileInputStream( "/gubo/dev/studio/slipwire/app/src/main/assets/job.xml" ) );

            final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware( false );
            final XmlPullParser parser = factory.newPullParser();
            parser.setInput( input, null );

            JobletContext currentjobletcontext = null;
            String text = null;

            int eventtype = parser.getEventType();
            while ( eventtype != XmlPullParser.END_DOCUMENT ) {
                switch ( parser.getEventType() ) {
                case XmlPullParser.START_TAG:
                {
                    final String tag = parser.getName().replace( '\r', ' ' ).replace( '\n', ' ' ).trim();
                    if ( "joblet".equals( tag ) || "joblet-mapping".equals( tag )) {
                        currentjobletcontext = new JobletContext();
                    }
                }
                break;
                case XmlPullParser.TEXT:
                    text = parser.getText().replace( '\r',' ' ).replace( '\n',' ' ).trim();
                    break;
                case XmlPullParser.END_TAG:
                {
                    final String tag = parser.getName().replace( '\r', ' ' ).replace( '\n',' ' ).trim();
                    if ( "joblet".equals( tag ) || "joblet-mapping".equals( tag )) {
                        final JobletContext existingjobletcontext = map.get( currentjobletcontext.joblet_name );
                        if ( existingjobletcontext != null ) {
                            if ( currentjobletcontext.joblet_class == null ) { currentjobletcontext.joblet_class = existingjobletcontext.joblet_class; }
                            if ( currentjobletcontext.url_pattern == null ) { currentjobletcontext.url_pattern = existingjobletcontext.url_pattern; }
                        }
                        map.put( currentjobletcontext.joblet_name,currentjobletcontext );
                        currentjobletcontext = null;
                    } else if ( "joblet-name".equals( tag ) ) {
                        currentjobletcontext.joblet_name = text;
                    } else if ( "joblet-class".equals( tag ) ) {
                        currentjobletcontext.joblet_class = text;
                    } else if ( "url-pattern".equals( tag ) ) {
                        currentjobletcontext.url_pattern = text;
                    }
                }
                break;
                }
                eventtype = parser.next();
            }

            DBG.v( map );
        } catch ( Exception x ) {
            DBG.m( x );
        } finally {
            Util.close( input );
        }
    }

    public static void main( final String [] args ) {
        new JobletHandler( null );
    }
}
