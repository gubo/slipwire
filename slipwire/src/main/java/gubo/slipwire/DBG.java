
package gubo.slipwire;

/**
 * DBG<br>
 * Thread-safe.
 */
public class DBG
{
    private static Log log = new SystemLog();
    private static boolean verbose = false;
    private static boolean trace = false;
    private static String tag = "DBG";

    /**
     *
     * @return
     */
    public static Log getLog() {
        final Log l = log;
        return l;
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
        final String t = tag;
        return t;
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
     * @return
     */
    public static boolean isVerbose() {
        final boolean v = verbose;
        return v;
    }

    /**
     *
     * @param verbose
     */
    public static void setVerbose( final boolean verbose ) {
        DBG.verbose = verbose;
    }

    /**
     *
     * @return
     */
    public static boolean isTrace() {
        final boolean t = trace;
        return t;
    }

    /**
     *
     * @param trace
     */
    public static void setTrace( final boolean trace ) {
        DBG.trace = trace;
    }

    /**
     *
     * @param o
     */
    public static void m( final Object o ) {
        final Log l = log;
        if ( l != null ) {
            l.d( tag,( o != null ? o.toString() : null ) );
        }
    }

    /**
     *
     * @param o
     */
    public static void w( final Object o ) {
        final Log l = log;
        if ( l != null ) {
            l.w( tag,( o != null ? o.toString() : null ) );
        }
    }

    /**
     *
     * @param o
     */
    public static void v( final Object o ) {
        final Log l = log;
        final boolean v = verbose;
        if ( v && (l != null) ) {
            l.v( tag,( o != null ? o.toString() : null ) );
        }
    }

    /**
     *
     * @param o
     */
    public static void t( final Object o ) {
        final Log l = log;
        final boolean t = trace;
        if ( t && (l != null) ) {
            l.v( tag,( o != null ? o.toString() : null ) );
        }
    }

    /**
     *
     * @param x
     */
    public static void m( final Throwable x ) {
        final Log l = log;
        if ( l != null ) {
            l.e( tag,"XXX",x );
        }
    }

    public static class SystemLog implements Log
    {
        @Override
        public void d( final String tag,final String msg ) {
            System.out.println( tag + ThreadID.string() + msg );
        }

        @Override
        public void v( final String tag,final String msg ) {
            System.out.println( tag + ThreadID.string() + msg );
        }

        @Override
        public void w( final String tag,final String msg ) {
            System.err.println( tag + ThreadID.string() + msg );
        }

        @Override
        public void e( final String tag,final String msg,final Throwable x ) {
            System.out.println( tag + ThreadID.string() + "XXX" );
            if ( x != null ) { x.printStackTrace(); }
        }

    }

    public static class AndroidLog implements Log
    {
        @Override
        public void d( final String tag,final String msg ) {
            android.util.Log.d( tag+ThreadID.string(),msg );
        }

        @Override
        public void v( final String tag,final String msg ) {
            android.util.Log.v( tag+ThreadID.string(),msg );
        }

        @Override
        public void w( final String tag,final String msg ) {
            android.util.Log.w( tag+ThreadID.string(),msg );
        }

        @Override
        public void e( final String tag,final String msg,final Throwable x ) {
            android.util.Log.d( tag+ThreadID.string(),msg,x );
        }
    }

    private static class ThreadID
    {
        private static final java.util.Map<Long,String> threadmap = new java.util.HashMap<>( 15 );

        private static String string() {
            final long threadId = Long.valueOf( Thread.currentThread().getId() );
            String string = threadmap.get( threadId );
            if ( string == null ) {
                string = "[" + Thread.currentThread().getId() + "] ";
                threadmap.put( threadId,string );
            }
            return string;
        }

        private ThreadID() {}
    }

    private DBG() {}

    public static void main( final String [] args ) {
        DBG.setVerbose( true );
        DBG.setTrace( false );
        DBG.setTag( "MOCK-DBG" );
        DBG.m( "message" );
        DBG.v( "verbose " + DBG.isVerbose() );
        DBG.t( "trace " + DBG.isTrace() );
        DBG.m( new Exception( "bogus exception" ) );
    }
}
