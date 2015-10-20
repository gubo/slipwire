
package gubo.sample.presenter;

import javax.inject.*;

import rx.*;
import rx.functions.*;
import rx.android.schedulers.*;

import gubo.slipwire.*;
import gubo.sample.event.*;

/*
 * - tells adapter to indicate network activity happening or not
 */
public class NetworkActivityPresenter implements Presenter
{
    public interface Display extends Presenter.Display
    {
        public void setActive( boolean active );
    }

    private final EventBus eventbus;

    private NetworkActivityPresenter.Display display;
    private Subscription eventsubscription;
    private int currentactivecount;

    @Inject
    public NetworkActivityPresenter( final EventBus eventbus ) throws IllegalArgumentException {
        if ( eventbus == null ) { throw new IllegalArgumentException(); }

        this.eventbus = eventbus;

        final Action1<Event> DA1 = new Action1<Event>() {
            @Override public void call( final Event event ) { onEvent( event ); }
        };
        eventsubscription = eventbus.toObserverable().subscribeOn( AndroidSchedulers.mainThread() ).subscribe( DA1 );
        currentactivecount = 0;
    }

    @Override
    public <D extends Presenter.Display> void bind( D display ) {
        DBG.v( "NetworkActivityPresenter.bind " + display );

        this.display = ( NetworkActivityPresenter.Display)display;

        refresh();
    }

    @Override
    public void release() {
        if ( eventsubscription != null ) {
            eventsubscription.unsubscribe();
            eventsubscription = null;
        }

        DBG.v( "NetworkActivityPresenter.release" );
    }

    private void onEvent( final Event event ) {
        if ( event instanceof NetworkActivityEvent ) {
            final NetworkActivityEvent networkactivityevent = ( NetworkActivityEvent)event;
            currentactivecount += ( networkactivityevent.active ? +1 : -1 );
            if ( currentactivecount < 0 ) { currentactivecount = 0; }
            refresh();
        }
    }

    private void refresh() {
        if ( this.display != null ) {
            this.display.setActive( currentactivecount > 0 );
        }
    }
}
