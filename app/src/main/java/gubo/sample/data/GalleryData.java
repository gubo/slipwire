
package gubo.sample.data;

import android.net.*;
import android.content.*;

import gubo.slipwire.*;

public class GalleryData extends Data
{
    private static final int requestcode = RequestCode.newCode();

    public Uri uri;

    public GalleryData( final Object origin ) {
        super( origin );
    }

    public static int myIntention() { return GalleryData.requestcode; }
    public static boolean isMyIntention( final int requestcode ) { return GalleryData.requestcode == requestcode; }

    public static GalleryData fromIntent( final Intent intent ) throws IllegalArgumentException {
        if ( intent == null ) { throw new IllegalArgumentException(); }

        final GalleryData gallerydata = new GalleryData( GalleryData.class );

        gallerydata.uri = intent.getData();

        return gallerydata;
    }
}
