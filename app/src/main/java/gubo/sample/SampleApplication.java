
package gubo.sample;

import android.app.*;
import android.os.Build;

import gubo.slipwire.*;

/*
 * - expose instance
 * - create and expose SampleComponent
 */
public class SampleApplication extends Application
{
    private static SampleApplication instance;

    private SampleComponent samplecomponent;

    @Override
    public void onCreate() {
        super.onCreate();
        DBG.m( " " );
        DBG.m( "SLIPWIRE V" + Util.version( this ) );
        DBG.m( "AND V" + Build.VERSION.SDK_INT );
        DBG.m( "SampleApplication.onCreate" );

        instance = this;

        VOLLEY.startup( this );

        samplecomponent = DaggerSampleComponent.create();
    }

    static SampleApplication getInstance() { return instance; }

    SampleComponent getSampleComponent() { return this.samplecomponent; }

    @Override
    public void onTerminate() {
        super.onTerminate();

        VOLLEY.shutdown();

        instance = null;

        DBG.m( "SampleApplication.onTerminate" );
    }
}
