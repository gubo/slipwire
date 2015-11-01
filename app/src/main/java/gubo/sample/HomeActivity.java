
package gubo.sample;

import javax.inject.*;

import android.os.*;
import android.app.*;
import android.view.*;
import android.content.*;
import android.support.v7.app.*;
import android.support.v7.app.ActionBar;

import gubo.slipwire.*;
import gubo.sample.data.*;
import gubo.sample.event.*;

/*
 * - inflate home layout
 * - create HomeFragment
 * - customize ActionBar
 * - send Data for Activity results
 * - send Events for item selections
 */
public class HomeActivity extends AppCompatActivity
{
    /*
     * Dagger cant inject private fields
     */
    @Inject EventBus ieventbus;
    @Inject DataBus idatabus;

    private final Latch newtask = new Latch();

    private EventBus eventbus;
    private DataBus databus;

    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        DBG.m( "HomeActivity.onCreate" );

        final SampleComponent samplecomponent = SampleApplication.getSampleComponent();
        samplecomponent.inject( this );
        eventbus = ieventbus;
        databus = idatabus;
        ieventbus = null;
        idatabus = null;

        setContentView( R.layout.home );

        final FragmentManager fragmentmanager = getFragmentManager();
        Fragment fragment = fragmentmanager.findFragmentByTag( HomeFragment.class.getName() );
        if ( fragment == null ) {
            fragment = new HomeFragment();
            fragmentmanager.beginTransaction().add( fragment,HomeFragment.class.getName() ).commit();
        }

        final ActionBar actionbar = getSupportActionBar();
        actionbar.setLogo( R.mipmap.ic_logo );
        actionbar.setDisplayUseLogoEnabled( true );
        actionbar.setDisplayShowHomeEnabled( true );
    }

    @Override
    public boolean onCreateOptionsMenu( final Menu menu ) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.actionbar,menu );

        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected( final MenuItem item ) {
        boolean handled = false;

        switch ( item.getItemId() ) {
        case R.id.menu_search:
            eventbus.send( new gubo.sample.event.SearchEvent( HomeActivity.class ) );
            handled = true;
            break;
        case R.id.menu_books:
            eventbus.send( new gubo.sample.event.BooksEvent( HomeActivity.class,this ) );
            handled = true;
            break;
        case R.id.menu_test:
            eventbus.send( new gubo.sample.event.TestEvent( HomeActivity.class ) );
            handled = true;
            break;
        default:
            handled = super.onOptionsItemSelected( item );
            break;
        }

        return handled;
    }

    private class Busy implements Runnable
    {
        final boolean busy;

        Busy( final boolean busy ) { this.busy = busy; }

        @Override public void run() {
            eventbus.send( new BusyEvent( HomeActivity.class,busy ) );
            getWindow().getDecorView().postDelayed( new Busy( false ),1500L );
        }
    }

    /*
     * http://developer.android.com/reference/android/app/Activity.html#onActivityResult(int, int, android.content.Intent)
     * "You will receive this call immediately before onResume() when your activity is re-starting."
     */
    @Override
    protected void onActivityResult( final int requestcode,final int resultcode,final Intent intent ) {
        Data data = null;

        try {
            if ( resultcode == Activity.RESULT_OK ) {
                if ( GalleryData.isMyIntention( requestcode ) ) {
                    data = GalleryData.fromIntent( intent );
                }
            }
        } catch ( Exception x ) {
            DBG.m( x );
        }

        if ( data != null ) {
            databus.send( data );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        DBG.m( "HomeActivity.onResume" );

        if ( newtask.trip() ) { getWindow().getDecorView().postDelayed( new Busy( true ),25L ); }
    }

    @Override
    protected void onPause() {
        super.onPause();

        DBG.m( "HomeActivity.onPause" );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DBG.m( "HomeActivity.onDestroy" );
    }
}
