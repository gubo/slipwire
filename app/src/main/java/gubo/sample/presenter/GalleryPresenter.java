
package gubo.sample.presenter;

import rx.*;
import rx.functions.*;
import rx.android.schedulers.*;

import gubo.slipwire.*;
import gubo.sample.data.*;

/*
 *
 */
public class GalleryPresenter implements Presenter,DataSource
{
    public interface Display extends Presenter.Display,DataSink
    {
        void setVisible( boolean visible );
        void release();
    }

    private final EventBus eventbus;
    private final DataBus databus;

    private GalleryPresenter.Display display;
    private Subscription datasubscription;

    private GalleryData currentgallerydata;

    public GalleryPresenter( final EventBus eventbus,final DataBus databus ) throws IllegalArgumentException {
        if ( (eventbus == null) || (databus == null) ) { throw new IllegalArgumentException(); }

        this.eventbus = eventbus;
        this.databus = databus;

        final Action1<Data> DA1 = new Action1<Data>() {
            @Override public void call( final Data data ) { onData( data ); }
        };
        datasubscription = databus.toObserverable().subscribeOn( AndroidSchedulers.mainThread() ).subscribe( DA1 );

        currentgallerydata = null;
    }

    @Override
    public <D extends Presenter.Display> void bind( D display ) {
        DBG.v( "GalleryPresenter.bind " + display );

        if ( this.display != null ) {
            this.display.release();
            this.display = null;
        }

        this.display = ( GalleryPresenter.Display )display;

        refresh();
    }

    @Override
    public void release() {
        if ( datasubscription != null ) {
            datasubscription.unsubscribe();
            datasubscription = null;
        }

        if ( this.display != null ) {
            this.display.release();
            this.display = null;
        }

        currentgallerydata = null;

        DBG.v( "GalleryPresenter.release" );
    }

    @Override public Data getDataFor( final int position ) { return currentgallerydata; }
    @Override public void getReadyFor( final int position,final int count ) {}

    private void onData( final Data data ) {
        if ( data instanceof GalleryData ) {
            currentgallerydata = ( GalleryData)data;
        }
        refresh();
    }

    private void refresh() {
        if ( display != null ) {
            display.setVisible( currentgallerydata != null );
            display.setItemCount( 1 );
            display.setPosition( 0 );
        }
    }
}
