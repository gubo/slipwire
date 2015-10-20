
package gubo.sample;

import javax.inject.*;

import android.os.*;
import android.app.*;
import android.view.*;
import android.support.v7.app.*;
import android.support.v7.app.ActionBar;

import gubo.slipwire.*;
import gubo.sample.event.*;

/*
 * - has a EventBus
 * - inflate home layout
 * - create HomeFragment
 * - customize ActionBar
 * - send Events for item selections
 */
public class HomeActivity extends AppCompatActivity
{
    /*
     * Dagger cant inject private fields
     */
    @Inject EventBus ieventbus;

    private EventBus eventbus;
    private boolean launched;

    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        DBG.m( "HomeActivity.onCreate" );

        final String startedby = getIntent().getStringExtra( "startedby" );
        launched =  ( LaunchActivity.class.getName().equals( startedby ) );
        getIntent().putExtra( "startedby",( String)null );

        final SampleComponent samplecomponent = SampleApplication.getInstance().getSampleComponent();
        samplecomponent.inject( this );
        eventbus = ieventbus;
        ieventbus = null;

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
        case R.id.action_search:
            eventbus.send( new gubo.sample.event.SearchEvent( HomeActivity.class ) );
            handled = true;
            break;
        case R.id.action_books:
            eventbus.send( new gubo.sample.event.BooksEvent( HomeActivity.class,this ) );
            handled = true;
            break;
        default:
            handled = super.onOptionsItemSelected( item );
            break;
        }

        return handled;
    }

    @Override
    protected void onResume() {
        super.onResume();

        DBG.m( "HomeActivity.onResume" );

        STARTBUSY: {
            final Runnable action = new Runnable() {
                @Override public void run() {
                    eventbus.send( new BusyEvent( HomeActivity.class,true ) );
                }
            };
            if ( launched ) { getWindow().getDecorView().postDelayed( action,25L ); }
        }

        FREE: {
            final Runnable action = new Runnable() {
                @Override public void run() {
                    eventbus.send( new BusyEvent( HomeActivity.class,false ) );
                }
            };
            if ( launched ) { getWindow().getDecorView().postDelayed( action,1500L ); }
        }
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
