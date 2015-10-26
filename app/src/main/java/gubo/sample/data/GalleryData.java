
package gubo.sample.data;

import android.net.*;
import android.content.*;

import gubo.slipwire.*;

public class GalleryData extends Data
{
    public static final int REQUESTCODE = 900;

    public Uri uri;

    public GalleryData( final Object origin ) {
        super( origin );
    }

    public static GalleryData fromIntent( final Intent intent ) throws IllegalArgumentException {
        if ( intent == null ) { throw new IllegalArgumentException(); }

        final GalleryData gallerydata = new GalleryData( GalleryData.class );

        gallerydata.uri = intent.getData();

        return gallerydata;
    }
}
