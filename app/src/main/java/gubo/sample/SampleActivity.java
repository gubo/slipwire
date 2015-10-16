
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
 * - inflate sample layout
 * - create SampleFragment
 * - customize ActionBar
 * - send Events for item selections
 */
public class SampleActivity extends AppCompatActivity
{
    /*
     * Dagger cant inject private fields
     */
    @Inject EventBus ieventbus;

    private EventBus eventbus;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        DBG.m( "SampleActivity.onCreate" );

        final SampleComponent samplecomponent = SampleApplication.getInstance().getSampleComponent();
        samplecomponent.inject( this );
        eventbus = ieventbus;
        ieventbus = null;

        setContentView( R.layout.sample );

        final FragmentManager fragmentmanager = getFragmentManager();
        Fragment fragment = fragmentmanager.findFragmentByTag( SampleFragment.class.getName() );
        if ( fragment == null ) {
            fragment = new SampleFragment();
            fragmentmanager.beginTransaction().add( fragment,SampleFragment.class.getName() ).commit();
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
        boolean handled = super.onOptionsItemSelected( item );

        switch ( item.getItemId() ) {
        case R.id.action_search:
            eventbus.send( new gubo.sample.event.SearchEvent( SampleActivity.class ) );
            handled = true;
            break;
        case R.id.action_books:
            eventbus.send( new gubo.sample.event.BooksEvent( SampleActivity.class ) );
            handled = true;
            break;
        default:
            break;
        }

        return handled;
    }

    @Override
    protected void onResume() {
        super.onResume();

        DBG.m( "SampleActivity.onResume" );

        STARTBUSY: {
            final Runnable action = new Runnable() {
                @Override public void run() {
                    eventbus.send( new BusyEvent( SampleActivity.class,true ) );
                }
            };
            getWindow().getDecorView().postDelayed( action,25L );
        }

        FREE: {
            final Runnable action = new Runnable() {
                @Override public void run() {
                    eventbus.send( new BusyEvent( SampleActivity.class,false ) );
                }
            };
            getWindow().getDecorView().postDelayed( action,1000L );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        DBG.m( "SampleActivity.onPause" );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DBG.m( "SampleActivity.onDestroy" );
    }
}
