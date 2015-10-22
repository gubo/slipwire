
package gubo.sample;

import javax.inject.*;

import android.os.*;
import android.app.*;
import android.content.*;

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
    private Intent serverintent;

    @Override
    public void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        DBG.m( "HomeFragment.onCreate" );

        final SampleComponent samplecomponent = SampleApplication.getInstance().getSampleComponent();
        samplecomponent.inject( this );
        homemanager = ihomemanager;
        ihomemanager = null;

        serverintent = new Intent( getActivity(),gubo.slipwire.Server.class );
        serverintent.putExtra( "JobletFactory", gubo.sample.joblet.JobletFactory.class.getName() );
        getActivity().startService( serverintent );

        homemanager.manage();

        setRetainInstance( true );
    }

    @Override
    public void onResume() {
        super.onResume();

        DBG.m( "HomeFragment.onResume" );

        /*
         * here we must first discover the local server port, then can bind homemanager
         */
        final Context context = getActivity();
        final ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected( final ComponentName name,final IBinder service ) {
                final Server.Binder serverbinder = ( Server.Binder ) service;
                final Server server = serverbinder.getService();
                final int port = server.getPort();
                DBG.m( "HomeFragment: port=" + port );
                context.unbindService( this );
                homemanager.setJobletPort( port );
                homemanager.bind( getActivity() );
            }
            @Override
            public void onServiceDisconnected( final ComponentName name ) {
                context.unbindService( this );
            }
        };
        context.bindService( serverintent,connection,0 );
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

        getActivity().stopService( serverintent );
        serverintent = null;

        DBG.m( "HomeFragment.onDestroy" );
    }
}
