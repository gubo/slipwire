
package gubo.sample.presenter;

import rx.*;
import rx.functions.*;
import rx.android.schedulers.*;

import gubo.slipwire.*;
import gubo.sample.data.*;

/*
 * - update display when receive RandomLandSatData
 */
public class RandomLandSatPresenter implements Presenter,DataSource
{
    public interface Display extends Presenter.Display,DataSink
    {
        public void release();
    }

    private final EventBus eventbus;
    private final DataBus databus;

    private RandomLandSatPresenter.Display display;
    private RandomLandSatData currentlandsatdata;
    private Subscription datasubscription;

    public RandomLandSatPresenter( final EventBus eventbus,final DataBus databus ) throws IllegalArgumentException {
        if ( (eventbus == null) || (databus == null) ) { throw new IllegalArgumentException(); }

        this.eventbus = eventbus;
        this.databus = databus;

        final Action1<Data> DA1 = new Action1<Data>() {
            @Override public void call( final Data data ) { onData( data ); }
        };
        datasubscription = databus.toObserverable().subscribeOn( AndroidSchedulers.mainThread() ).subscribe( DA1 );

        currentlandsatdata = new RandomLandSatData( null );
        currentlandsatdata.id = "";
        currentlandsatdata.date = "";
        currentlandsatdata.url = "";
        currentlandsatdata.name = "";
        currentlandsatdata.countrycode = "";
        currentlandsatdata.latitude = "";
        currentlandsatdata.longitude = "";
    }

    @Override public Data getDataFor( final int position ) { return currentlandsatdata; }
    @Override public void getReadyFor( final int position,final int count ) {}

    @Override
    public <D extends Presenter.Display> void bind( D display ) {
        DBG.v( "RandomLandSatPresenter.bind " + display );

        if ( this.display != null ) {
            this.display.release();
        }

        this.display = ( RandomLandSatPresenter.Display)display;

        refresh();
    }

    @Override
    public void release() {
        if ( datasubscription != null ) {
            datasubscription.unsubscribe();
            datasubscription = null;
        }

        if ( display != null ) {
            display.release();
        }

        DBG.v( "RandomLandSatPresenter.release" );

    }

    private void onData( final Data data ) {
        if ( data instanceof RandomLandSatData ) {
            currentlandsatdata = ( RandomLandSatData)data;
            refresh();
        }
    }

    private void refresh() {
        if ( display != null ) {
            display.setPosition( 0 );
            display.setItemCount( 1 );
        }
    }
}
