
package gubo.sample.presenter;

import rx.*;
import rx.functions.*;
import rx.android.schedulers.*;

import gubo.slipwire.*;
import gubo.sample.event.*;

/*
 * - allows user to cancel all network_activity
 */
public class CancelAllPresenter implements Presenter
{
    public interface CancelListener
    {
        public void onCancel();
    }

    public interface Display extends Presenter.Display
    {
        public void setCancelListener( CancelAllPresenter.CancelListener listener );
        public void setEnabled( boolean enabled );
    }

    private final EventBus eventbus;

    private CancelAllPresenter.Display display;
    private Subscription eventsubscription;
    private int currentactivecount;

    public CancelAllPresenter( final EventBus eventbus ) throws IllegalArgumentException {
        if ( eventbus == null ) { throw new IllegalArgumentException(); }

        final Action1<Event> EA1 = new Action1<Event>() {
            @Override public void call( final Event event ) { onEvent( event ); }
        };
        eventsubscription = eventbus.toObserverable().subscribeOn( AndroidSchedulers.mainThread() ).subscribe( EA1 );

        this.eventbus = eventbus;

        currentactivecount = 0;
    }

    @Override
    public <D extends Presenter.Display> void bind( D display ) {
        DBG.v( "CancelAllPresenter.bind " + display );

        this.display = ( CancelAllPresenter.Display)display;

        if ( this.display != null ) {
            final CancelAllPresenter.CancelListener listener = new CancelAllPresenter.CancelListener() {
                @Override public void onCancel() { CancelAllPresenter.this.onCancel(); }
            };
            this.display.setCancelListener( listener );
        }

        refresh();
    }

    @Override
    public void release() {
        if ( eventsubscription != null ) {
            eventsubscription.unsubscribe();
            eventsubscription = null;
        }

        DBG.v( "CancelAllPresenter.release" );
    }

    private void onEvent( final Event event ) {
        if ( event instanceof NetworkActivityEvent ) {
            final NetworkActivityEvent networkactivityevent = ( NetworkActivityEvent)event;
            currentactivecount += ( networkactivityevent.active ? +1 : -1 );
            if ( currentactivecount < 0 ) { currentactivecount = 0; }
        }
        refresh();
    }

    private void onCancel() {
        eventbus.send( new CancelAllEvent( CancelAllPresenter.class ) );
    }

    private void refresh() {
        if ( display != null ) {
            display.setEnabled( currentactivecount > 0 );
        }
    }
}
