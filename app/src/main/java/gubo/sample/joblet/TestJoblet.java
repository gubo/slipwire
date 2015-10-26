
package gubo.sample.joblet;

import java.util.*;

import com.google.gson.*;

import gubo.slipwire.*;

/*
 *
 */
public class TestJoblet implements Joblet
{
    static class _Test
    {
        public String result;
    }

    static class _Result
    {
        _Test test;
    }

    private ContextBroker contextbroker;
    private Map<String,String> parameters;

    @Override
    public void setContextBroker( final ContextBroker contextbroker ) {
        this.contextbroker = contextbroker;
    }

    @Override
    public void setParameters( final Map<String,String> parameters ) {
        this.parameters = parameters;
    }

    @Override
    public String perform() {
        DBG.m( "TestJoblet.perform[" + Thread.currentThread().getId() + "]" );

        String json = "{}";

        final _Result _result = new _Result();
        final _Test _test = new _Test();
        _result.test = _test;

        try {
            final String _debug = ( String)parameters.get( "debug" );
            final String _delay = ( String)parameters.get( "delay" );

            final Test test = new GalleryTest( contextbroker,parameters );
            test.run();
            _result.test.result = test.getResult();

            try {
                if ( _delay != null ) {
                    int delay = Integer.parseInt( _delay );
                    delay = Math.min( delay,7500 );
                    delay = Math.max( delay, 0 );
                    Thread.sleep( delay );
                }
            } catch ( Exception x ) {
                DBG.m( x );
            }
        } catch ( Exception x ) {
            _result.test.result = x.getClass().getSimpleName();
            DBG.m( x );
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        json = gson.toJson( _result,_Result.class );

        return json;
    }
}
