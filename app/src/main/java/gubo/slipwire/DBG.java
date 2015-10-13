
package gubo.slipwire;

import android.util.*;

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
     * @param o
     */
    public static void m( final Object o ) { Log.d( "DBG",""+o ); }

    /**
     *
     * @param o
     */
    public static void w( final Object o ) { Log.d( "DBG","<< WARNING >> "+o ); }

    /**
     *
     * @param o
     */
    public static void v( final Object o ) { if ( verbose ) { DBG.m( o ); } }

    /**
     *
     * @param x
     */
    public static void m( final Throwable x ) { Log.d( "DBG", "XXX", x ); }

    private DBG() {}
}
