
package gubo.sample.presenter;

import javax.inject.*;

import rx.*;
import rx.functions.*;
import rx.android.schedulers.*;

import gubo.slipwire.*;
import gubo.sample.data.*;

/*
 *
 */
public class StatusPresenter implements Presenter,DataSource
{
    public interface Display extends Presenter.Display,DataSink
    {
    }

    private final EventBus eventbus;
    private final DataBus databus;

    private StatusPresenter.Display display;
    private Subscription datasubscription;
    private StatusData currentstatusdata;

    @Inject
    public StatusPresenter( final EventBus eventbus,final DataBus databus ) throws IllegalArgumentException {
        if ( (eventbus == null) || (databus == null) ) { throw new IllegalArgumentException(); }

        this.eventbus = eventbus;
        this.databus = databus;

        final Action1<Data> DA1 = new Action1<Data>() {
            @Override public void call( final Data data ) { onData( data ); }
        };
        datasubscription = databus.toObserverable().subscribeOn( AndroidSchedulers.mainThread() ).subscribe( DA1 );

        currentstatusdata = new StatusData( StatusPresenter.class );
    }

    @Override
    public <D extends Presenter.Display> void bind( D display ) {
        DBG.v( "StatusPresenter.bind " + display );

        this.display = ( StatusPresenter.Display)display;

        refresh();
    }

    @Override
    public void release() {
        DBG.v( "StatusPresenter.release" );
    }

    @Override public Data getDataFor( final int position ) { return currentstatusdata; }
    @Override public void getReadyFor( final int position,final int count ) {}

    private void onData( final Data data ) {
        if ( data instanceof StatusData ) {
            currentstatusdata = ( StatusData)data;
        }
        refresh();
    }

    private void refresh() {
        if ( display != null ) {
            display.setItemCount( 1 );
            display.setPosition( 0 );
        }
    }
}
