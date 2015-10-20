
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
 * - bind/release Presenters
 * - event mediator: invoke action based on event
 */
class HomeManager implements Manageable
{
    private final EventBus eventbus;
    private final DataBus databus;

    private Subscription eventsubscription;

    private BusyPresenter busypresenter;
    private PendingPresenter pendingpresenter;
    private CancelAllPresenter cancelallpresenter;
    private NetworkActivityPresenter networkactivitypresenter;
    private FetchCurrentWeatherPresenter fetchcurrentweatherpresenter;
    private FetchRandomWikiPresenter fetchrandomwikipresenter;
    private CurrentWeatherPresenter currentweatherpresenter;
    private RandomWikiPresenter randomwikipresenter;

    @Inject
    HomeManager( final EventBus eventbus, final DataBus databus ) throws IllegalArgumentException {
        if ( (eventbus == null) || (databus == null) ) { throw new IllegalArgumentException(); }

        this.eventbus = eventbus;
        this.databus = databus;
    }

    @Override
    public void manage() {
        DBG.m( "HomeManager.manage" );

        final Action1<Event> EA1 = new Action1<Event>() {
            @Override public void call( final Event event ) { onEvent( event ); }
        };
        eventsubscription = eventbus.toObserverable().subscribeOn( AndroidSchedulers.mainThread() ).subscribe( EA1 );

        /*
         * TODO: inject ?
         */
        busypresenter = new BusyPresenter( eventbus );
        pendingpresenter = new PendingPresenter( eventbus );
        cancelallpresenter = new CancelAllPresenter( eventbus );
        networkactivitypresenter = new NetworkActivityPresenter( eventbus );
        fetchcurrentweatherpresenter = new FetchCurrentWeatherPresenter( eventbus,databus );
        fetchrandomwikipresenter = new FetchRandomWikiPresenter( eventbus,databus );
        currentweatherpresenter = new CurrentWeatherPresenter( eventbus,databus );
        randomwikipresenter = new RandomWikiPresenter( eventbus,databus );
    }

    @Override
    public void bind( final android.app.Activity activity ) {
        DBG.m( "HomeManager.bind" );

        busypresenter.bind( new BusyAdapter( activity.findViewById( R.id.home_busy ) ) );
        pendingpresenter.bind( new PendingAdapter( activity.findViewById( R.id.home_statusbar_pending ) ) );
        cancelallpresenter.bind( new CancelAllAdapter( activity.findViewById( R.id.home_statusbar_cancel_all ) ) );
        networkactivitypresenter.bind( new NetworkActivityAdapter( activity.findViewById( R.id.home_statusbar_network_activity ) ) );
        fetchcurrentweatherpresenter.bind( new FetchCurrentWeatherAdapter( fetchcurrentweatherpresenter,activity.findViewById( R.id.home_menubar_fetch_currentweather ) ) );
        fetchrandomwikipresenter.bind( new FetchRandomWikiAdapter( fetchrandomwikipresenter, activity.findViewById( R.id.home_menubar_fetch_randomwiki ) ) );
        currentweatherpresenter.bind( new CurrentWeatherAdapter( currentweatherpresenter,activity.findViewById( R.id.home_currentweather ) ) );
        randomwikipresenter.bind( new RandomWikiAdapter( randomwikipresenter, activity.findViewById( R.id.home_randomwiki ) ) );
    }

    @Override
    public void unbind() {
        randomwikipresenter.bind( null );
        currentweatherpresenter.bind( null );
        fetchrandomwikipresenter.bind( null );
        fetchcurrentweatherpresenter.bind( null );
        networkactivitypresenter.bind( null );
        cancelallpresenter.bind( null );
        pendingpresenter.bind( null );
        busypresenter.bind( null );

        DBG.m( "HomeManager.unbind" );
    }

    @Override
    public void unmanage() {
        randomwikipresenter.release();
        currentweatherpresenter.release();
        fetchrandomwikipresenter.release();
        fetchcurrentweatherpresenter.release();
        networkactivitypresenter.release();
        cancelallpresenter.release();
        pendingpresenter.release();
        busypresenter.release();

        if ( eventsubscription != null ) {
            eventsubscription.unsubscribe();
            eventsubscription = null;
        }

        DBG.m( "HomeManager.unmanage" );
    }

    private void onEvent( final Event event ) {
        if ( event instanceof FetchCurrentWeatherEvent ) { new FetchCurrentWeatherAction( event.getOrigin(),eventbus,databus ).invoke(); }
        if ( event instanceof FetchRandomWikiEvent ) { new FetchRandomWikiAction( event.getOrigin(),eventbus,databus ).invoke(); }

        if ( event instanceof BooksEvent ) {
            final BooksEvent booksevent = ( BooksEvent)event;
            new BooksAction( event.getOrigin(),booksevent.activity ).invoke();
        }
    }
}
