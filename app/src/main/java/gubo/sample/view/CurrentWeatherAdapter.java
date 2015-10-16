
package gubo.sample.view;

import android.view.*;
import android.widget.*;

import javax.inject.*;

import gubo.slipwire.*;
import gubo.sample.data.*;
import gubo.sample.presenter.*;

/*
 *
 */
public class CurrentWeatherAdapter implements CurrentWeatherPresenter.Display,DataSink
{
    private final DataSource datasource;
    private final View view;

    @Inject // TODO ?
    public CurrentWeatherAdapter( final DataSource<Data> datasource,final View view ) throws IllegalArgumentException {
        if ( (datasource == null) || (view == null) ) { throw new IllegalArgumentException(); }

        this.datasource = datasource;
        this.view = view;
    }

    @Override public void release() {}

    @Override public void setItemCount( final int itemcount ) {}

    @Override
    public void setPosition( final int position ) {
        final CurrentWeatherData currentweatherdata = ( CurrentWeatherData)datasource.getDataFor( position );
        bind( currentweatherdata );
    }

    private void bind( final CurrentWeatherData currentweatherdata ) {
        if ( currentweatherdata == null ) {
            DBG.w( "NULL WEATHER DATA" );
            return;
        }
        try {
            final com.android.volley.toolbox.NetworkImageView iconnetworkimageview = ( com.android.volley.toolbox.NetworkImageView)view.findViewById( R.id.sample_currentweather_status_icon );
            if ( currentweatherdata.iconurl != null ) {
                iconnetworkimageview.setImageUrl( currentweatherdata.iconurl, VOLLEY.getInstance().getImageLoader() );
            }

            final TextView headingtextview = ( TextView )view.findViewById( R.id.sample_currentweather_status_heading );
            headingtextview.setText( ( "" + currentweatherdata.heading ).toUpperCase() );

            final TextView temperaturetextview = ( TextView )view.findViewById( R.id.sample_currentweather_status_temperature );
            temperaturetextview.setText( ( int)farenheit( currentweatherdata.temp ) + "\u00B0F    " + ( int)currentweatherdata.temp + "\u00B0C" );
        } catch ( Exception x ) {
            DBG.m( x );
        }
    }

    private float farenheit( final float c ) {
        final float f = ( c * (9F / 5F) ) + 32F;
        return f;
    }
}
