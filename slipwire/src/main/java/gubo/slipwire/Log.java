
package gubo.slipwire;

/**
 * Created by JEFF on 5/18/2016.
 */
public interface Log
{
    void d( String tag,String msg );
    void v( String tag,String msg );
    void w( String tag,String msg );
    void e( String tag,String msg,Throwable x );
}
