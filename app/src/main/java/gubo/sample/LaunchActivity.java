
package gubo.sample;

import android.os.*;
import android.app.*;
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

        final Runnable action = new Runnable() {
            @Override
            public void run() {
                home();
            }
        };
        getWindow().getDecorView().postDelayed( action, 1500L );
    }

    private void home() {
        final Intent intent = new Intent( this,HomeActivity.class );
        intent.putExtra( "startedby",LaunchActivity.class.getName() );
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
