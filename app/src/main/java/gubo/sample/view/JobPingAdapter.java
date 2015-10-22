
package gubo.sample.view;

import android.view.*;
import android.widget.*;
import android.graphics.drawable.*;
import android.support.v4.content.*;

import gubo.slipwire.*;
import gubo.sample.presenter.*;
import gubo.sample.data.*;

/*
 *
 */
public class JobPingAdapter implements JobPingPresenter.Display,DataSink,View.OnClickListener
{
    private JobPingPresenter.JobListener listener;
    private final DataSource datasource;
    private final View view;

    public JobPingAdapter( final DataSource<Data> datasource,final View view ) throws IllegalArgumentException {
        if ( (datasource == null) || (view == null) ) { throw new IllegalArgumentException(); }

        this.datasource = datasource;
        this.view = view;

        this.view.setOnClickListener( this );
    }

    @Override
    public void setJobListener( final JobPingPresenter.JobListener listener ) {
        this.listener = listener;
    }

    @Override
    public void setActive( final boolean active ) {
        try {
            final TextView textview = ( TextView )view;
            final Drawable[] drawables = textview.getCompoundDrawables();
            final Drawable drawableL = drawables[ 0 ];
            final Drawable drawableR = ContextCompat.getDrawable( view.getContext(), active ? R.drawable.ic_cancel : R.drawable.ic_play );
            textview.setCompoundDrawablesWithIntrinsicBounds( drawableL,null,drawableR,null );
        } catch ( Exception x ) {
            DBG.m( x );
        }
    }

    @Override public void release() {}

    @Override public void setItemCount( final int itemcount ) {}

    @Override
    public void setPosition( final int position ) {
        try {
            final Object object = datasource.getDataFor( position );
            if ( object instanceof PingData ) {
                final TextView textview = ( TextView )view;
                final Drawable [] drawables = textview.getCompoundDrawables();
                final Drawable drawableL = ContextCompat.getDrawable( view.getContext(),R.drawable.ic_ping );
                textview.setCompoundDrawablesWithIntrinsicBounds( drawableL,null,drawables[ 2 ],null );
            }
        } catch ( Exception x ) {
            DBG.m( x );
        }
    }

    @Override
    public void onClick( final View v ) {
        if ( listener != null ) {
            listener.onJob();
        }
    }
}
