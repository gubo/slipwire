
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
public class RandomLandSatAdapter implements RandomLandSatPresenter.Display,DataSink
{
    private final DataSource datasource;
    private final View view;

    @Inject // TODO ?
    public RandomLandSatAdapter( final DataSource<Data> datasource,final View view ) throws IllegalArgumentException {
        if ( (datasource == null) || (view == null) ) { throw new IllegalArgumentException(); }

        this.datasource = datasource;
        this.view = view;
    }

    @Override public void release() {}

    @Override public void setItemCount( final int itemcount ) {}

    @Override
    public void setPosition( final int position ) {
        final RandomLandSatData randomlandsatdata = ( RandomLandSatData)datasource.getDataFor( position );
        bind( randomlandsatdata );
    }

    private void bind( final RandomLandSatData randomlandsatdata ) {
        if ( randomlandsatdata == null ) {
            DBG.w( "NULL LANDSAT DATA" );
            return;
        }
        try {
            final com.android.volley.toolbox.NetworkImageView imagenetworkimageview = ( com.android.volley.toolbox.NetworkImageView)view.findViewById( R.id.home_randomlandsat_image );
            if ( randomlandsatdata.url != null ) {
//                imagenetworkimageview.setErrorImageResId( R.drawable.ic_errorimage );
//                imagenetworkimageview.setDefaultImageResId( R.drawable.ic_defaultimage );
                imagenetworkimageview.setImageUrl( randomlandsatdata.url, VOLLEY.getInstance().getImageLoader() );
            }

            final TextView nametextview = ( TextView )view.findViewById( R.id.home_randomlandsat_name );
            nametextview.setText( randomlandsatdata.name + " " + randomlandsatdata.countrycode );

            final TextView latitudelongitudetextview = ( TextView )view.findViewById( R.id.home_randomlandsat_latitudelongitude );
            latitudelongitudetextview.setText( randomlandsatdata.latitude + "\t\t" + randomlandsatdata.longitude );
        } catch ( Exception x ) {
            DBG.m( x );
        }
    }
}
