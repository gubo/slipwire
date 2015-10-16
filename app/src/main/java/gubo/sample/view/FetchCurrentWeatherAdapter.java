
package gubo.sample.view;

import javax.inject.*;

import android.view.*;
import android.widget.*;
import android.graphics.drawable.*;
import android.support.v4.content.*;

import gubo.slipwire.*;
import gubo.sample.data.*;
import gubo.sample.presenter.*;

/*
 *
 */
public class FetchCurrentWeatherAdapter implements FetchCurrentWeatherPresenter.Display,DataSink,View.OnClickListener
{
    private FetchCurrentWeatherPresenter.FetchListener listener;
    private final DataSource datasource;
    private final View view;

    @Inject // TODO ?
    public FetchCurrentWeatherAdapter( final DataSource<Data> datasource,final View view ) throws IllegalArgumentException {
        if ( (datasource == null) || (view == null) ) { throw new IllegalArgumentException(); }

        this.datasource = datasource;
        this.view = view;

        this.view.setOnClickListener( this );
    }

    @Override
    public void setFetchListener( final FetchCurrentWeatherPresenter.FetchListener listener ) {
        this.listener = listener;
    }

    @Override
    public void setActive( final boolean active ) {
        if ( view instanceof TextView ) {
            final TextView textview = ( TextView )view;
            final Drawable [] drawables = textview.getCompoundDrawables();
            final Drawable drawableL = drawables[ 0 ];
            final Drawable drawableR = ContextCompat.getDrawable( view.getContext(),active ? R.drawable.ic_cancel : R.drawable.ic_play );
            textview.setCompoundDrawablesWithIntrinsicBounds( drawableL,null,drawableR,null );
        }
    }

    @Override public void release() {}

    @Override public void setItemCount( final int itemcount ) {}

    @Override public void setPosition( final int position ) {
        try {
            final Object object = datasource.getDataFor( position );
            if ( object instanceof CurrentWeatherData ) {
                final CurrentWeatherData currentweatherdata = ( CurrentWeatherData)object;
                int rid = interpret( currentweatherdata.id );
                final TextView textview = ( TextView )view;
                final Drawable [] drawables = textview.getCompoundDrawables();
                final Drawable drawableL = ContextCompat.getDrawable( view.getContext(),rid );
                textview.setCompoundDrawablesWithIntrinsicBounds( drawableL,null,drawables[ 2 ],null );
            }
        } catch ( Exception x ) {
            DBG.m( x );
        }
    }

    @Override
    public void onClick( final View v ) {
        if ( listener != null ) {
            listener.onFetch();
        }
    }

    /*
     * http://openweathermap.org/weather-conditions
     */
    private int interpret( final int id ) {
        int rid = R.drawable.ic_blank;

        if ( (id >= 200) && (id <= 232) ) { rid = R.drawable.ic_cloud; }
        if ( (id >= 300) && (id <= 321) ) { rid = R.drawable.ic_rain; }
        if ( (id >= 500) && (id <= 531) ) { rid = R.drawable.ic_rain; }
        if ( (id >= 600) && (id <= 622) ) { rid = R.drawable.ic_snow; }
        if ( (id >= 701) && (id <= 781) ) { rid = R.drawable.ic_warning; }
        if ( id == 800 ) { rid = R.drawable.ic_sun; }
        if ( (id >= 801) && (id <= 804) ) { rid = R.drawable.ic_snow; }
        if ( (id >= 900) && (id <= 906) ) { rid = R.drawable.ic_warning; }
        if ( (id >= 951) && (id <= 956) ) { rid = R.drawable.ic_sun; }
        if ( (id >= 957) && (id <= 962) ) { rid = R.drawable.ic_warning; }

        return rid;
    }
}
