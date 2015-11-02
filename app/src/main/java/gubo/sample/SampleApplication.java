
package gubo.sample;

import android.os.*;
import android.app.*;

import gubo.slipwire.*;

/*
 * - create and expose SampleComponent
 */
public class SampleApplication extends Application
{
    private static SampleApplication instance;

    private SampleComponent samplecomponent;

    /*
     * API 14
     */
    final Application.ActivityLifecycleCallbacks callbacks = new Application.ActivityLifecycleCallbacks() {
        @Override public void onActivityCreated( final Activity activity,final Bundle savedInstanceState ) {}
        @Override public void onActivityStarted( final Activity activity ) { SampleContextBroker.instance.setActivityContext( activity ); }
        @Override public void onActivityResumed( final Activity activity ) {}
        @Override public void onActivityPaused( final Activity activity ) {}
        @Override public void onActivityStopped( final Activity activity ) {}
        @Override public void onActivitySaveInstanceState( final Activity activity,final Bundle outState ) {}
        @Override public void onActivityDestroyed( final Activity activity ) {}
    };

    @Override
    public void onCreate() {
        super.onCreate();
        DBG.m( "\u220E\u220E\u220E" );
        DBG.m( "SLIPWIRE V" + Util.version( this ) );
        DBG.m( "AND V" + Build.VERSION.SDK_INT );
        DBG.m( "SampleApplication.onCreate" );

        com.squareup.leakcanary.LeakCanary.install( this );

        instance = this;
        samplecomponent = DaggerSampleComponent.create();

        SampleContextBroker.instance.setApplicationContext( this );
        SampleContextBroker.instance.setActivityContext( null );
        registerActivityLifecycleCallbacks( callbacks );
    }

    static SampleComponent getSampleComponent() {
        return instance.samplecomponent;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        unregisterActivityLifecycleCallbacks( callbacks );
        SampleContextBroker.instance.setActivityContext( null );
        SampleContextBroker.instance.setApplicationContext( null );

        samplecomponent = null;
        instance = null;

        DBG.m( "SampleApplication.onTerminate" );
    }
}
