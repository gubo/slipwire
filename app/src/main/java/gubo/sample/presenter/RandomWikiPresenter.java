
package gubo.sample.presenter;

import rx.*;
import rx.functions.*;
import rx.android.schedulers.*;

import gubo.slipwire.*;
import gubo.sample.data.*;

/*
 * - update display when receive RandomWikiData
 */
public class RandomWikiPresenter implements Presenter,DataSource
{
    public interface Display extends Presenter.Display,DataSink
    {
        public void release();
    }

    private final EventBus eventbus;
    private final DataBus databus;

    private RandomWikiPresenter.Display display;
    private RandomWikiData currentwikidata;
    private Subscription datasubscription;

    public RandomWikiPresenter( final EventBus eventbus,final DataBus databus ) throws IllegalArgumentException {
        if ( (eventbus == null) || (databus == null) ) { throw new IllegalArgumentException(); }

        DBG.m( "RandomWikiPresenter" );

        this.eventbus = eventbus;
        this.databus = databus;

        final Action1<Data> DA1 = new Action1<Data>() {
            @Override public void call( final Data data ) { onData( data ); }
        };
        datasubscription = databus.toObserverable().subscribeOn( AndroidSchedulers.mainThread() ).subscribe( DA1 );

        currentwikidata = new RandomWikiData( null );
        currentwikidata.id = "";
        currentwikidata.title = "";
        currentwikidata.thumbnailurl = "";
    }

    @Override public Data getDataFor( final int position ) { return currentwikidata; }
    @Override public void getReadyFor( final int position,final int count ) {}

    @Override
    public <D extends Presenter.Display> void bind( D display ) {
        DBG.v( "RandomWikiPresenter.bind " + display );

        if ( this.display != null ) {
            this.display.release();
        }

        this.display = ( RandomWikiPresenter.Display)display;

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

        DBG.m( "RandomWikiPresenter.release" );

    }

    private void onData( final Data data ) {
        if ( data instanceof RandomWikiData ) {
            currentwikidata = ( RandomWikiData)data;
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
