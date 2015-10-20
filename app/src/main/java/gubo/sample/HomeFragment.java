
package gubo.sample;

import android.os.*;
import android.app.*;

import javax.inject.*;

import gubo.slipwire.*;

/*
 * - has a HomeManager
 * - bind/unbind the HomeManager
 * - retain instance to persist over HomeActivity lifecycle
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

        final SampleComponent samplecomponent = SampleApplication.getInstance().getSampleComponent();
        samplecomponent.inject( this );
        homemanager = ihomemanager;
        ihomemanager = null;

        homemanager.manage();

        setRetainInstance( true );
    }

    @Override
    public void onResume() {
        super.onResume();

        DBG.m( "HomeFragment.onResume" );

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

        DBG.m( "HomeFragment.onDestroy" );
    }
}
