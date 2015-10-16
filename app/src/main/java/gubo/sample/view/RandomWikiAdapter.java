
package gubo.sample.view;

import javax.inject.*;

import android.view.*;
import android.widget.*;

import gubo.slipwire.*;
import gubo.sample.data.*;
import gubo.sample.presenter.*;

/*
 *
 */
public class RandomWikiAdapter implements RandomWikiPresenter.Display,DataSink
{
    private final DataSource datasource;
    private final View view;

    @Inject // TODO ?
    public RandomWikiAdapter( final DataSource<Data> datasource,final View view ) throws IllegalArgumentException {
        if ( (datasource == null) || (view == null) ) { throw new IllegalArgumentException(); }

        this.datasource = datasource;
        this.view = view;
    }

    @Override public void release() {}

    @Override public void setItemCount( final int itemcount ) {}

    @Override
    public void setPosition( final int position ) {
        final RandomWikiData randomwikidata = ( RandomWikiData)datasource.getDataFor( position );
        bind( randomwikidata );
    }

    private void bind( final RandomWikiData randomwikidata ) {
        if ( randomwikidata == null ) {
            DBG.w( "NULL WIKI DATA" );
            return;
        }
        try {
            final com.android.volley.toolbox.NetworkImageView iconnetworkimageview = ( com.android.volley.toolbox.NetworkImageView)view.findViewById( R.id.sample_randomwiki_info_icon );
            if ( randomwikidata.thumbnailurl != null ) {
                iconnetworkimageview.setImageUrl( randomwikidata.thumbnailurl,VOLLEY.getInstance().getImageLoader() );
            }

            final TextView headingtextview = ( TextView )view.findViewById( R.id.sample_randomwiki_info_title );
            headingtextview.setText( ( "" + randomwikidata.title ).toUpperCase() );
        } catch ( Exception x ) {
            DBG.m( x );
        }
    }
}
