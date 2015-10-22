
package gubo.sample.joblet;

import java.io.*;
import java.util.*;

import gubo.slipwire.*;

class PingJoblet implements Joblet
{
    private static final String [] greetings = {
            "hello there",
            "howdy",
            "good morning",
            "hows it goin eh",
            "cheers",
            "good afternoon",
            "hey cheeky monkey"
    };

    private final Random random = new Random( System.currentTimeMillis() );

    @Override
    public Serializable perform() {
        final Ping ping = new Ping();

        final int rn = Math.abs( random.nextInt( greetings.length ) );
        ping.greeting = greetings[ rn ];

        return ping;
    }
}
