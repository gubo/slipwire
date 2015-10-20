
package gubo.sample;

import javax.inject.*;

import android.os.*;
import android.app.*;
import android.support.v7.app.*;

import gubo.slipwire.*;
import gubo.sample.event.*;

/*
 *
 */
public class BooksActivity extends AppCompatActivity
{
    /*
     * Dagger cant inject private fields
     */
    @Inject
    EventBus ieventbus;

    private EventBus eventbus;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        DBG.m( "BooksActivity.onCreate" );

        final SampleComponent samplecomponent = SampleApplication.getInstance().getSampleComponent();
        samplecomponent.inject( this );
        eventbus = ieventbus;
        ieventbus = null;

        setContentView( R.layout.books );

        final FragmentManager fragmentmanager = getFragmentManager();
        Fragment fragment = fragmentmanager.findFragmentByTag( BooksFragment.class.getName() );
        if ( fragment == null ) {
            fragment = new BooksFragment();
            fragmentmanager.beginTransaction().add( fragment,BooksFragment.class.getName() ).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DBG.m( "BooksActivity.onResume" );

        final Runnable action = new Runnable() {
            public void run() {
                DBG.m( "BooksActivity send restock ..." );
                eventbus.send( new BooksRestockEvent( BooksActivity.class ) );
            }
        };
        getWindow().getDecorView().postDelayed( action,1000 );
    }

    @Override
    protected void onPause() {
        super.onPause();
        DBG.m( "BooksActivity.onPause" );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBG.m( "BooksActivity.onDestroy" );
    }
}
