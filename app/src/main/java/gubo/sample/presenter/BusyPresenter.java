
package gubo.sample.presenter;

import rx.*;
import rx.functions.*;
import rx.android.schedulers.*;

import gubo.slipwire.*;
import gubo.sample.event.*;

/*
 * - update busy animation based on BusyEvents
 */
public class BusyPresenter implements Presenter
{
    public interface Display extends Presenter.Display
    {
        public void setActive( boolean active );
    }

    private final EventBus eventbus;

    private BusyPresenter.Display display;
    private Subscription eventsubscription;
    private int busycount;

    static class Unknown {}

    public BusyPresenter( final EventBus eventbus ) throws IllegalArgumentException {
        if ( eventbus == null ) {
            throw new IllegalArgumentException();
        }

        DBG.m( "BusyPresenter" );

        this.eventbus = eventbus;

        final Action1<Event> DA1 = new Action1<Event>() {
            @Override public void call( final Event event ) { onEvent( event ); }
        };
        eventsubscription = eventbus.toObserverable().subscribeOn( AndroidSchedulers.mainThread() ).subscribe( DA1 );
    }

    @Override
    public <D extends Presenter.Display> void bind( D display ) {
        DBG.v( "BusyPresenter.bind " + display );

        this.display = ( BusyPresenter.Display)display;

        refresh();
    }

    private void onEvent( final Event event ) {
        if ( event instanceof BusyEvent ) {
            final BusyEvent busyevent = ( BusyEvent)event;
            busycount += ( busyevent.busy ? +1 : -1 );
            if ( busycount < 0 ) { busycount = 0; }
        }
        refresh();
    }

    @Override
    public void release() {
        if ( eventsubscription != null ) {
            eventsubscription.unsubscribe();
            eventsubscription = null;
        }

        DBG.m( "BusyPresenter.release" );
    }

    private void refresh() {
        if ( display != null ) {
            display.setActive( busycount > 0 );
        }
    }
}
