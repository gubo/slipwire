
package gubo.sample.view;

import android.view.*;
import android.widget.*;

import gubo.slipwire.*;
import gubo.sample.data.*;
import gubo.sample.presenter.*;

/*
 *
 */
public class GalleryAdapter implements GalleryPresenter.Display
{
    private final DataSource datasource;
    private final View view;

    public GalleryAdapter( final DataSource<Data> datasource, final View view ) throws IllegalArgumentException {
        if ( (datasource == null) || (view == null) ) { throw new IllegalArgumentException(); }

        this.datasource = datasource;
        this.view = view;
    }

    @Override
    public void setVisible( final boolean visible ) {
        view.setVisibility( visible ? View.VISIBLE : View.INVISIBLE );
    }

    @Override public void release() {
        try {
            final ImageView imageview = ( ImageView)view.findViewById( R.id.home_gallery_picture );
            imageview.setImageBitmap( null );
        } catch ( Exception x ) {
            DBG.m( x );
        }
    }

    @Override public void setItemCount( final int itemcount ) {}

    @Override public void setPosition( final int position ) {
        try {
            final Object object = datasource.getDataFor( position );
            if ( object instanceof GalleryData ) {
                final GalleryData gallerydata = ( GalleryData)object;

                final ImageView imageview = ( ImageView)view.findViewById( R.id.home_gallery_picture );
                imageview.setImageURI( gallerydata.uri );
            }
        } catch ( Exception x ) {
            DBG.m( x );
        }
    }
}
