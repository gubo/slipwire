
package gubo.slipwire;

import java.io.*;
import java.util.*;

import android.util.*;
import android.content.*;
import android.graphics.*;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import com.google.gson.*;

/**
 *
 */
public class VOLLEY
{
    /**
     *
     * @param <T>
     */
    public static class GsonRequest<T> extends Request<T>
    {
        private final Gson gson = new Gson();
        private final Class<T> clazz;
        private final Map<String, String> headers;
        private final Response.Listener<T> listener;

        /**
         * Make a GET request and return a parsed object from JSON.
         *
         * @param url URL of the request to make
         * @param clazz Relevant class object, for Gson's reflection
         * @param headers Map of request headers
         */
        public GsonRequest( final String url,final Class<T> clazz,final Map<String, String> headers,final Response.Listener<T> listener,final Response.ErrorListener errorListener ) {
            super( Method.GET,url,errorListener);
            this.clazz = clazz;
            this.headers = headers;
            this.listener = listener;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return headers != null ? headers : super.getHeaders();
        }

        @Override
        protected void deliverResponse( final T response ) {
            listener.onResponse( response );
        }

        @Override
        protected Response<T> parseNetworkResponse( final NetworkResponse response ) {
            try {
                String json = new String( response.data, HttpHeaderParser.parseCharset( response.headers ) );
                return Response.success( gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders( response ) );
            } catch ( UnsupportedEncodingException e ) {
                return Response.error( new ParseError( e ) );
            } catch ( JsonSyntaxException e ) {
                return Response.error( new ParseError( e ) );
            }
        }
    }

    static class BitmapLoaderCache extends LruCache<String,Bitmap> implements ImageLoader.ImageCache
    {
        /**
         * @param max in this case, is the maximum sum of the sizes of the entries in this cache
         * @see LruCache( int size ) - size can have different meaning !
         */
        BitmapLoaderCache( final int max ) {
            super( max );
        }

        @Override
        public Bitmap getBitmap( final String url ) {
            return get( url );
        }

        /**
         *
         * @param url
         * @param bitmap
         * @return
         * @see LruCache( int size )
         */
        @Override
        protected int sizeOf( final String url,final Bitmap bitmap ) {
            if ( bitmap == null ) { return 0; }
            return bitmap.getRowBytes() * bitmap.getHeight();
        }

        @Override
        public void putBitmap( final String url,final Bitmap bitmap ) {
            put( url,bitmap );
        }
    }

    private static VOLLEY instance;

    private RequestQueue requestqueue;
    private ImageLoader imageloader;

    public static final void startup( final Context context ) throws IllegalArgumentException {
        Util.assertMainThread();
        DBG.m( "VOLLEY.instantiated" );
        if ( instance == null ) {
            instance = new VOLLEY();
            instance.initialize( context );
        }
    }

    public static final VOLLEY getInstance() throws IllegalArgumentException {
        Util.assertMainThread();
        return instance;
    }

    /**
     *
     * @param request
     * @param <T>
     */
    public <T> void queueRequest( Request<T> request ) {
        Util.assertMainThread();
        requestqueue.add( request );
    }

    /**
     *
     * @return
     */
    public ImageLoader getImageLoader() {
        Util.assertMainThread();
        return imageloader;
    }

    /**
     *
     * @param tag
     */
    public void cancel( final Object tag ) {
        Util.assertMainThread();
        requestqueue.cancelAll( tag );
    }

    /**
     *
     * @param filter
     */
    public void cancel( final RequestQueue.RequestFilter filter ) {
        Util.assertMainThread();
        requestqueue.cancelAll( filter );
    }

    /**
     *
     */
    public static void shutdown() {
        Util.assertMainThread();
        if ( instance != null ) {
            instance.requestqueue.stop();
            instance = null;
            DBG.m( "VOLLEY.shutdown" );
        }
    }

    /*
     * http://developer.android.com/training/volley/index.html
     */
    private void initialize( final Context context ) throws IllegalArgumentException {
        if ( context == null ) { throw new IllegalArgumentException(); }
        DBG.m( "VOLLEY.initialize" );
        try {
            requestqueue = Volley.newRequestQueue( context.getApplicationContext() );

            final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            final int screenWidth = displayMetrics.widthPixels;
            final int screenHeight = displayMetrics.heightPixels;
            final int screenBytes = screenWidth * screenHeight * 4; // 4 bytes per pixel
            final int cachesize = ( screenBytes * 3 ); // 3 screens worth - google suggestion
            final ImageLoader.ImageCache imagecache = new BitmapLoaderCache( cachesize );
            DBG.m( "VOLLEY: imagecache = " + cachesize );

            imageloader = new ImageLoader( requestqueue,imagecache );
        } catch ( Exception x ) {
            DBG.m( x );
        }
    }

    private VOLLEY() {}
}
