
package gubo.sample.joblet;

import java.util.*;

import com.google.gson.*;

import gubo.slipwire.*;

/*
 *
 */
public class PingJoblet implements Joblet
{
    static class Ping
    {
        String greeting;
    }

    static class Result
    {
        Ping ping;
    }

    private static final Random random = new Random( System.currentTimeMillis() );

    private static final String [] greetings = {
            "howdy pardener",
            "good day mate",
            "konnichiwa",
            "hey stoopid",
            "guten tag",
            "happy birthday",
            "whats shakin bacon",
            "hallo dare",
            "nice to meet your acquaitence",
            "namaste",
            "aloha"
    };

    private ContextBroker contextbroker;
    private Map<String,String> parameters;

    @Override public void setContextBroker( final ContextBroker contextbroker ) {
        this.contextbroker = contextbroker;
    }

    @Override public void setParameters( final Map<String,String> parameters ) {
        this.parameters = parameters;
    }

    @Override
    public String perform() {
        DBG.m( "PingJoblet.perform[" + Thread.currentThread().getId() + "]" );

        String json = "{}";

        try {
            final String _debug = ( String)parameters.get( "debug" );
            final String _delay = ( String)parameters.get( "delay" );

            final Result result = new Result();
            final Ping ping = new Ping();

            final int rn = Math.abs( random.nextInt( greetings.length ) );
            ping.greeting = greetings[ rn ];
            result.ping = ping;

            try {
                if ( _delay != null ) {
                    int delay = Integer.parseInt( _delay );
                    delay = Math.min( delay,7500 );
                    delay = Math.max( delay,   0 );
                    Thread.sleep( delay );
                }
            } catch ( Exception x ) {
                DBG.m( x );
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            json = gson.toJson( result,Result.class );
        } catch ( Exception x ) {
            DBG.m( x );
        }

        return json;
    }
}
