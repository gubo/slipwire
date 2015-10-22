
package gubo.sample.view;

import android.view.*;
import android.widget.*;

import gubo.slipwire.*;
import gubo.sample.data.*;
import gubo.sample.presenter.*;

/*
 *
 */
public class StatusAdapter implements StatusPresenter.Display,DataSink
{
    private final DataSource datasource;
    private final View view;

    public StatusAdapter( final DataSource<Data> datasource, final View view ) throws IllegalArgumentException {
        if ( (datasource == null) || (view == null) ) { throw new IllegalArgumentException(); }

        this.datasource = datasource;
        this.view = view;
    }

    @Override public void setItemCount( final int itemcount ) {}

    @Override
    public void setPosition( final int position ) {
        try {
            final Data data = datasource.getDataFor( position );
            if ( data instanceof StatusData ) {
                final StatusData statusdata = ( StatusData)data;
                final TextView messagetextview = ( TextView)view.findViewById( R.id.home_statusbar_status_message );
                messagetextview.setText( statusdata.message != null ? statusdata.message.toUpperCase() : "" );
            }
        } catch ( Exception x ) {
            DBG.m( x );
        }
    }
}
