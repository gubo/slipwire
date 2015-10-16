
package gubo.sample.view;

import javax.inject.*;

import android.view.*;
import android.graphics.drawable.*;

import gubo.sample.presenter.*;

public class PendingAdapter implements PendingPresenter.Display
{
    private final View view;

    @Inject
    public PendingAdapter( final View view ) throws IllegalArgumentException {
        if ( view == null ) { throw new IllegalArgumentException(); }
        this.view = view;
    }

    @Override
    public void setActive( final boolean active ) {
        view.setAlpha( active ? 1F : .25F );
        view.setEnabled( active );
        final AnimationDrawable animation = ( AnimationDrawable )view.getBackground();
        if ( active ) { animation.start(); } else { animation.stop(); }
    }
}
