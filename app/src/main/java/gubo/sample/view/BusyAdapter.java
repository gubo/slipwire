
package gubo.sample.view;

import javax.inject.*;

import android.view.*;

import gubo.sample.presenter.*;

public class BusyAdapter implements BusyPresenter.Display
{
    private final View view;

    @Inject
    public BusyAdapter( final View view ) throws IllegalArgumentException {
        if ( view == null ) { throw new IllegalArgumentException(); }
        this.view = view;
    }

    @Override
    public void setActive( final boolean active ) {
        view.setVisibility( active ? View.VISIBLE : View.INVISIBLE );
    }
}
