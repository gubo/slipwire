
package gubo.sample;

import android.os.*;
import android.app.*;

import javax.inject.*;

import gubo.slipwire.*;

/*
 * - has a BooksManager
 * - bind/unbind the BooksManager
 * - retain instance to persist over BooksActivity lifecycle
 */
public class BooksFragment extends Fragment
{
    /*
     * Dagger cant inject private fields
     */
    @Inject BooksManager ibooksmanager;

    private BooksManager booksmanager;

    @Override
    public void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        DBG.m( "BooksFragment.onCreate" );

        final SampleComponent samplecomponent = SampleApplication.getSampleComponent();
        samplecomponent.inject( this );
        booksmanager = ibooksmanager;
        ibooksmanager = null;

        booksmanager.manage();

        setRetainInstance( true );
    }

    @Override
    public void onResume() {
        super.onResume();

        DBG.m( "BooksFragment.onResume" );

        booksmanager.bind( getActivity() );
    }

    @Override
    public void onPause() {
        super.onPause();

        booksmanager.unbind();

        DBG.m( "BooksFragment.onPause" );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        booksmanager.unmanage();

        DBG.m( "BooksFragment.onDestroy" );
    }
}
