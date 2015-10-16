
package gubo.sample;

import android.os.*;
import android.app.*;

import javax.inject.*;

import gubo.slipwire.*;

/*
 * - has a SampleManager
 * - bind/unbind the SampleManager
 * - retain instance to persist over SampleActivity lifecycle
 */
public class SampleFragment extends Fragment
{
    /*
     * Dagger cant inject private fields
     */
    @Inject SampleManager isamplemanager;

    private SampleManager samplemanager;

    @Override
    public void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        DBG.m( "SampleFragment.onCreate" );

        final SampleComponent samplecomponent = SampleApplication.getInstance().getSampleComponent();
        samplecomponent.inject( this );
        samplemanager = isamplemanager;
        isamplemanager = null;

        samplemanager.manage();

        setRetainInstance( true );
    }

    @Override
    public void onResume() {
        super.onResume();

        DBG.m( "SampleFragment.onResume" );

        samplemanager.bind( getActivity() );
    }

    @Override
    public void onPause() {
        super.onPause();

        samplemanager.unbind();

        DBG.m( "SampleFragment.onPause" );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        samplemanager.unmanage();

        DBG.m( "SampleFragment.onDestroy" );
    }
}
