
package gubo.sample.joblet;

import java.io.*;
import java.util.*;

import gubo.slipwire.*;

public class JobletFactory implements gubo.slipwire.JobletFactory
{
    private final Map<String,Class<? extends Joblet>> getmap = new HashMap<>();

    static class NOP implements Serializable {}

    static class NOPJoblet implements Joblet
    {
        @Override
        public Serializable perform() {
            return new NOP();
        }
    }

    public JobletFactory() {
        getmap.put( "/ping",PingJoblet.class );
    }

    @Override
    public Joblet newJoblet( final String method,final String resource,final Map<String, String> parameters ) {
        DBG.v( "JobletFactory.newJoblet " + method + " " + resource );

        Joblet joblet = new NOPJoblet();

        try {
            if ( "GET".equalsIgnoreCase( method ) ) {
                final Class<? extends Joblet> _joblet = getmap.get( resource );
                if ( _joblet != null ) {
                    joblet = _joblet.newInstance();
                }
            }
        } catch ( Exception x ) {
            DBG.m( x );
        }

        return joblet;
    }
}
