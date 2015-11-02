
package gubo.sample;

import android.os.*;
import android.app.*;
import android.view.*;
import android.content.*;

import gubo.slipwire.*;

/*
 *
 */
public class LaunchActivity extends Activity
{
    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        DBG.m( "LaunchActivity.onCreate" );

        setContentView( R.layout.launch );
    }

    @Override
    protected void onResume() {
        super.onResume();

        DBG.m( "LaunchActivity.onResume" );

        final View goview = findViewById( R.id.launch_go );
        if ( goview.getVisibility() != View.VISIBLE ) {
            final Runnable action = new Runnable() {
                @Override public void run() { home(); }
            };
            getWindow().getDecorView().postDelayed( action,750L );
        }
    }

    public void onGO( final View view ) {
        home();
    }

    private void home() {
        final Intent intent = new Intent( this,HomeActivity.class );
        startActivity( intent );
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

        DBG.m( "LaunchActivity.onPause" );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DBG.m( "LaunchActivity.onDestroy" );
    }
}
