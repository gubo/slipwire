
package gubo.slipwire;

/**
 *
 */
public class RequestCode
{
    private static int code = 900;

    /**
     *
     * @return a unique number
     */
    public static synchronized int newCode() {
        return code++;
    }

    private RequestCode() {}
}
