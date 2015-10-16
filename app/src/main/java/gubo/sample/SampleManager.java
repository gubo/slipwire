
package gubo.sample;

import javax.inject.*;

import rx.*;
import rx.functions.*;
import rx.android.schedulers.*;

import gubo.slipwire.*;
import gubo.sample.view.*;
import gubo.sample.event.*;
import gubo.sample.action.*;
import gubo.sample.presenter.*;

/*
 * - has a EventBus
 * - has a DataBus
 * - subscribe to EventBus
 * - subscribe to DataBus
 * - manage/bind Presenters
 * - event mediator: invoke action based on event
 */
class SampleManager implements Manageable
{
    private final EventBus eventbus;
    private final DataBus databus;

    private Subscription eventsubscription;

    private BusyPresenter busypresenter;
    private PendingPresenter pendingpresenter;
    private FetchCurrentWeatherPresenter fetchcurrentweatherpresenter;
    private FetchRandomWikiPresenter fetchrandomwikipresenter;
    private CurrentWeatherPresenter currentweatherpresenter;
    private RandomWikiPresenter randomwikipresenter;

    @Inject
    SampleManager( final EventBus eventbus,final DataBus databus ) throws IllegalArgumentException {
        if ( (eventbus == null) || (databus == null) ) { throw new IllegalArgumentException(); }

        this.eventbus = eventbus;
        this.databus = databus;
    }

    @Override
    public void manage() {
        DBG.m( "SampleManager.manage" );

        final Action1<Event> EA1 = new Action1<Event>() {
            @Override public void call( final Event event ) { onEvent( event ); }
        };
        eventsubscription = eventbus.toObserverable().subscribeOn( AndroidSchedulers.mainThread() ).subscribe( EA1 );

        /*
         * TODO: inject ?
         */
        busypresenter = new BusyPresenter( eventbus );
        pendingpresenter = new PendingPresenter( eventbus );
        fetchcurrentweatherpresenter = new FetchCurrentWeatherPresenter( eventbus,databus );
        fetchrandomwikipresenter = new FetchRandomWikiPresenter( eventbus,databus );
        currentweatherpresenter = new CurrentWeatherPresenter( eventbus,databus );
        randomwikipresenter = new RandomWikiPresenter( eventbus,databus );
    }

    @Override
    public void bind( final android.app.Activity activity ) {
        DBG.m( "SampleManager.bind" );

        busypresenter.bind( new BusyAdapter( activity.findViewById( R.id.sample_busy ) ) );
        pendingpresenter.bind( new PendingAdapter( activity.findViewById( R.id.sample_pending ) ) );
        fetchcurrentweatherpresenter.bind( new FetchCurrentWeatherAdapter( fetchcurrentweatherpresenter,activity.findViewById( R.id.sample_menubar_fetch_currentweather ) ) );
        fetchrandomwikipresenter.bind( new FetchRandomWikiAdapter( fetchrandomwikipresenter, activity.findViewById( R.id.sample_menubar_fetch_randomwiki ) ) );
        currentweatherpresenter.bind( new CurrentWeatherAdapter( currentweatherpresenter,activity.findViewById( R.id.sample_currentweather ) ) );
        randomwikipresenter.bind( new RandomWikiAdapter( randomwikipresenter,activity.findViewById( R.id.sample_randomwiki ) ) );
    }

    private void onEvent( final Event event ) {
        if ( event instanceof FetchCurrentWeatherEvent ) { new FetchCurrentWeatherAction( event.getOrigin(),eventbus,databus ).invoke(); }
        if ( event instanceof FetchRandomWikiEvent ) { new FetchRandomWikiAction( event.getOrigin(),eventbus,databus ).invoke(); }
    }

    @Override
    public void unbind() {
        randomwikipresenter.bind( null );
        currentweatherpresenter.bind( null );
        fetchrandomwikipresenter.bind( null );
        fetchcurrentweatherpresenter.bind( null );
        pendingpresenter.bind( null );
        busypresenter.bind( null );

        DBG.m( "SampleManager.unbind" );
    }

    @Override
    public void unmanage() {
        randomwikipresenter.release();
        currentweatherpresenter.release();
        fetchrandomwikipresenter.release();
        fetchcurrentweatherpresenter.release();
        pendingpresenter.release();
        busypresenter.release();

        if ( eventsubscription != null ) {
            eventsubscription.unsubscribe();
            eventsubscription = null;
        }

        DBG.m( "SampleManager.unmanage" );
    }
}
