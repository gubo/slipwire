
package gubo.slipwire;

/**
 *
 */
public class DBG
{
    /**
     *
     */
    public static boolean verbose = false;

    /**
     *
     */
    public static boolean trace = false;

    /**
     *
     * @return
     */
    public static Log getLog() {
        return log;
    }

    /**
     *
     * @param log
     */
    public static void setLog( final Log log ) {
        DBG.log = log;
    }

    /**
     *
     * @return
     */
    public static String getTag() {
        return tag;
    }

    /**
     *
     * @param tag
     */
    public static void setTag( final String tag ) {
        DBG.tag = tag;
    }

    /**
     *
     * @param o
     */
    public static void m( final Object o ) {
        if ( log == null ) { return; }
        log.d( tag,( o != null ? o.toString() : null ) );
    }

    /**
     *
     * @param o
     */
    public static void w( final Object o ) {
        if ( log == null ) { return; }
        log.w( tag,( o != null ? o.toString() : null ) );
    }

    /**
     *
     * @param o
     */
    public static void v( final Object o ) {
        if ( log == null ) { return; }
        if ( verbose ) {
            log.v( tag,( o != null ? o.toString() : null ) );
        }
    }

    /**
     *
     * @param o
     */
    public static void t( final Object o ) {
        if ( log == null ) { return; }
        if ( trace ) {
            log.v( tag,( o != null ? o.toString() : null ) );
        }
    }

    /**
     *
     * @param x
     */
    public static void m( final Throwable x ) {
        if ( log == null ) { return; }
        log.e( tag,"XXX",x );
    }

    public static class Android implements Log
    {
        @Override
        public void d( final String tag,final String msg ) {
            android.util.Log.d( tag,msg );
        }

        @Override
        public void v( final String tag,final String msg ) {
            android.util.Log.v( tag,msg );
        }

        @Override
        public void w( final String tag,final String msg ) {
            android.util.Log.w( tag,msg );
        }

        @Override
        public void e( final String tag,final String msg,final Throwable x ) {
            android.util.Log.d( tag,msg,x );
        }
    }

    private static Log log = new DBG.Android();
    private static String tag = "DBG";

    private DBG() {}
}
