
package gubo.sample.presenter;

import java.util.*;

import rx.*;
import rx.functions.*;
import rx.android.schedulers.*;

import gubo.slipwire.*;
import gubo.sample.event.*;

/*
 * - update pending animation based on PendingEvents
 */
public class PendingPresenter implements Presenter
{
    public interface Display extends Presenter.Display
    {
        public void setActive( boolean active );
    }

    private final Set<Object> pendings = new HashSet<>();
    private final EventBus eventbus;

    private PendingPresenter.Display display;
    private Subscription eventsubscription;

    static class Unknown {}

    public PendingPresenter( final EventBus eventbus ) throws IllegalArgumentException {
        if ( eventbus == null ) { throw new IllegalArgumentException(); }

        this.eventbus = eventbus;

        final Action1<Event> DA1 = new Action1<Event>() {
            @Override public void call( final Event event ) { onEvent( event ); }
        };
        eventsubscription = eventbus.toObserverable().subscribeOn( AndroidSchedulers.mainThread() ).subscribe( DA1 );
    }

    @Override
    public <D extends Presenter.Display> void bind( D display ) {
        DBG.v( "PendingPresenter.bind " + display );

        this.display = ( PendingPresenter.Display)display;

        refresh();
    }

    private void onEvent( final Event event ) {
        if ( event instanceof PendingEvent ) {
            final PendingEvent pendingevent = ( PendingEvent)event;
            Object origin = pendingevent.getOrigin();
            if ( origin == null ) { origin = PendingPresenter.Unknown.class; }
            if ( pendingevent.pending ) {
                pendings.add( origin );
            } else {
                pendings.remove( origin );
            }
        } else if ( event instanceof PendingClearEvent ) {
            pendings.clear();
        }
        refresh();
    }

    @Override
    public void release() {
        if ( eventsubscription != null ) {
            eventsubscription.unsubscribe();
            eventsubscription = null;
        }

        DBG.v( "PendingPresenter.release" );
    }

    private void refresh() {
        if ( display != null ) {
            display.setActive( !pendings.isEmpty() );
        }
    }
}
