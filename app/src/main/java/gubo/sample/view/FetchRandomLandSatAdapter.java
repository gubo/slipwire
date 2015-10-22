
package gubo.sample.view;

import android.view.*;
import android.widget.*;
import android.graphics.drawable.*;
import android.support.v4.content.*;

import javax.inject.*;

import gubo.slipwire.*;
import gubo.sample.data.*;
import gubo.sample.presenter.*;

/*
 *
 */
public class FetchRandomLandSatAdapter implements FetchRandomLandSatPresenter.Display,DataSink,View.OnClickListener
{
    private FetchRandomLandSatPresenter.FetchListener listener;
    private final DataSource datasource;
    private final View view;

    @Inject // TODO ?
    public FetchRandomLandSatAdapter( final DataSource<Data> datasource, final View view ) throws IllegalArgumentException {
        if ( (datasource == null) || (view == null) ) { throw new IllegalArgumentException(); }
        this.datasource = datasource;
        this.view = view;
        this.view.setOnClickListener( this );
    }

    @Override
    public void setFetchListener( final FetchRandomLandSatPresenter.FetchListener listener ) {
        this.listener = listener;
    }

    @Override
    public void setActive( final boolean active ) {
        if ( view instanceof TextView ) {
            final TextView textview = ( TextView )view;
            final Drawable [] drawables = textview.getCompoundDrawables();
            final Drawable drawableL = drawables[ 0 ];
            final Drawable drawableR = ContextCompat.getDrawable( view.getContext(), active ? R.drawable.ic_cancel : R.drawable.ic_play );
            textview.setCompoundDrawablesWithIntrinsicBounds( drawableL,null,drawableR,null );
        }
    }

    @Override public void release() {}

    @Override public void setItemCount( final int itemcount ) {}

    @Override public void setPosition( final int position ) {
        try {
            final Object object = datasource.getDataFor( position );
            if ( object instanceof RandomLandSatData ) {
                final RandomLandSatData randomlandsatdata = ( RandomLandSatData)object;
                final TextView textview = ( TextView )view;
                final Drawable [] drawables = textview.getCompoundDrawables();
                final Drawable drawableL = ContextCompat.getDrawable( view.getContext(),R.drawable.ic_landsat );
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
}
