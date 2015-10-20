
package gubo.sample.view;

import android.view.*;
import android.graphics.drawable.*;

import gubo.sample.presenter.*;

/*
 * - turn network activity animated button on/off
 */
public class NetworkActivityAdapter implements NetworkActivityPresenter.Display
{
    private final View view;

    public NetworkActivityAdapter( final View view ) throws IllegalArgumentException {
        if ( view == null ) { throw new IllegalArgumentException(); }
        this.view = view;
    }

    @Override
    public void setActive( final boolean active ) {
        view.setAlpha( active ? 1F : .5F );
        view.setEnabled( active );
        final AnimationDrawable animation = ( AnimationDrawable )view.getBackground();
        if ( active ) { animation.start(); } else { animation.stop(); }
    }
}
