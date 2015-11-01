
package gubo.sample.view;

import java.io.*;

import android.net.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.graphics.*;

import rx.*;
import rx.subscriptions.*;
import rx.android.schedulers.*;

import gubo.slipwire.*;
import gubo.sample.data.*;
import gubo.sample.presenter.*;

/*
 *
 */
public class GalleryAdapter implements GalleryPresenter.Display
{
    private static final int THUMBNAIL_SIZE = 64;

    private final CompositeSubscription compositesubscription = new CompositeSubscription();
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
            compositesubscription.unsubscribe();

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
                thumbnail( gallerydata.uri );
            }
        } catch ( Exception x ) {
            DBG.m( x );
        }
    }

    private void thumbnail( final Uri uri ) {
        final Context context = view.getContext();

        final Observable<Bitmap> observable = Observable.create(
                new Observable.OnSubscribe<Bitmap>() {
                    @Override
                    public void call( final Subscriber<? super Bitmap> subscriber ) {
                        final Bitmap bitmap = createthumbnail( context,uri );
                        subscriber.onNext( bitmap );
                        subscriber.onCompleted();
                    }
                }
        );

        final Subscriber<Bitmap> subscriber = new Subscriber<Bitmap>() {
            @Override
            public void onNext( final Bitmap bitmap ) {
                DBG.m( "GalleryAdapter.thumbnail.onNext " + bitmap );
                final ImageView imageview = ( ImageView)view.findViewById( R.id.home_gallery_picture );
                imageview.setImageBitmap( bitmap );
            }
            @Override public void onCompleted() {
                DBG.m( "GalleryAdapter.thumbnail.onCompleted" );
            }
            @Override public void onError( Throwable e ) {
                DBG.m( "GalleryAdapter.thumbnail.onError " + e.getMessage() );
            }
        };

        compositesubscription.add( observable.subscribeOn( AndroidSchedulers.mainThread() ).subscribe( subscriber ) );
    }

    /*
     * @ref http://stackoverflow.com/questions/3879992/get-bitmap-from-an-uri-android
     * pjv
     */
    private Bitmap createthumbnail( final Context context,final Uri uri ) {
        Bitmap bitmap = null;

        InputStream input = null;
        try {
            input = context.getContentResolver().openInputStream( uri );
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inJustDecodeBounds = true;
            options.inDither = true;
            BitmapFactory.decodeStream( input,null,options );
            input.close();

            if ( (options.outWidth == -1) || (options.outHeight == -1) ) {
                throw new IOException( "bad image" );
            }

            int originalSize = ( options.outHeight > options.outWidth ? options.outHeight : options.outWidth );

            double ratio = ( originalSize > THUMBNAIL_SIZE ? (originalSize / THUMBNAIL_SIZE) : 1.0 );

            options = new BitmapFactory.Options();
            options.inSampleSize = getPowerOfTwoForSampleRatio( ratio );
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inDither = true;

            input = context.getContentResolver().openInputStream( uri );
            bitmap = BitmapFactory.decodeStream( input,null,options );
        } catch ( Exception x ) {
            DBG.m( x );
        } finally {
            Util.close( input );
        }

        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio( final double ratio ){
        int k = Integer.highestOneBit( ( int)Math.floor( ratio ) );
        if ( k == 0 ) { k = 1; }
        return k;
    }
}
