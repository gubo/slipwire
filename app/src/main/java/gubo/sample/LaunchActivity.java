
package gubo.sample;

import android.os.*;
import android.app.*;
import android.view.*;

import gubo.slipwire.*;

public class LaunchActivity extends Activity
{
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        DBG.m( "LaunchActivity.onCreate" );

        final View view = new View( this );
        view.setBackgroundColor( 0xFF440044 );
        this.setContentView( view );
    }

    @Override
    protected void onResume() {
        super.onResume();
        DBG.m( "LaunchActivity.onResume" );
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
