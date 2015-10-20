
package gubo.sample.view;

import android.view.*;

import gubo.sample.presenter.*;

/*
 * - update cancel_all button
 */
public class CancelAllAdapter implements CancelAllPresenter.Display,View.OnClickListener
{
    private CancelAllPresenter.CancelListener listener;
    private View view;

    public CancelAllAdapter( final View view ) throws IllegalArgumentException {
        if ( view == null ) { throw new IllegalArgumentException(); }

        this.view = view;

        view.setOnClickListener( this );
    }

    @Override
    public void setCancelListener( final CancelAllPresenter.CancelListener listener ) {
        this.listener = listener;
    }

    @Override
    public void setEnabled( final boolean enabled ) {
        view.setAlpha( enabled ? 1F : .15F );
        view.setEnabled( enabled );
    }

    @Override
    public void onClick( final View v ) {
        if ( listener != null ) {
            listener.onCancel();
        }
    }
}
