
package gubo.sample;

import java.lang.ref.*;

import android.content.*;

import gubo.slipwire.*;

/*
 *
 */
class SampleContextBroker implements ContextBroker
{
    static final SampleContextBroker instance = new SampleContextBroker();

    private class Brokerage
    {
        Context application;
        Context activity;
    }

    private final Brokerage brokerage = new Brokerage();

    @Override
    public WeakReference<Context> getApplicationContext() {
        synchronized ( brokerage ) {
            final WeakReference<Context> weakreference = ( brokerage.application != null ? new WeakReference<Context>( brokerage.application ) : null );
            return weakreference;
        }
    }

    void setApplicationContext( final Context context ) {
        synchronized ( brokerage ) {
            brokerage.application = context;
        }
        DBG.m( "SampleContextBroker<application=" + brokerage.application + ">" );
    }

    @Override
    public WeakReference<Context> getActivityContext() {
        synchronized ( brokerage ) {
            final WeakReference<Context> weakreference = ( brokerage.activity != null ? new WeakReference<Context>( brokerage.activity ) : null );
            return weakreference;
        }
    }

    void setActivityContext( final Context context ) {
        synchronized ( brokerage ) {
            brokerage.activity = context;
        }
        DBG.m( "SampleContextBroker<activity=" + brokerage.activity + ">" );
    }

    private SampleContextBroker() {}
}
