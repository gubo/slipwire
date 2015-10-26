
package gubo.sample;

import javax.inject.*;

import android.os.*;
import android.app.*;

import gubo.slipwire.*;

/*
 * - bind/unbind the HomeManager
 * - retain instance to persist over HomeActivity lifecycle
 * - startup/shutdown VOLLEY AND JETTY
 */
public class HomeFragment extends Fragment
{
    /*
     * Dagger cant inject private fields
     */
    @Inject
    HomeManager ihomemanager;

    private HomeManager homemanager;

    @Override
    public void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        DBG.m( "HomeFragment.onCreate" );

        final SampleComponent samplecomponent = SampleApplication.getSampleComponent();
        samplecomponent.inject( this );
        homemanager = ihomemanager;
        ihomemanager = null;

        JETTY.startup( SampleContextBroker.instance );
        VOLLEY.startup( getActivity().getApplicationContext() );

        homemanager.manage();

        setRetainInstance( true );
    }

    @Override
    public void onResume() {
        super.onResume();

        DBG.m( "HomeFragment.onResume" );

        homemanager.setPort( JETTY.getPort() );
        homemanager.bind( getActivity() );
    }

    @Override
    public void onPause() {
        super.onPause();

        homemanager.unbind();

        DBG.m( "HomeFragment.onPause" );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        homemanager.unmanage();
        homemanager = null;

        SampleContextBroker.instance.setActivityContext( null );

        VOLLEY.shutdown();
        JETTY.shutdown();

        DBG.m( "HomeFragment.onDestroy" );
    }
}
