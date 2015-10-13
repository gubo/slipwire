
package gubo.slipwire;

/**
 *
 */
public interface JobletFactory
{
    /**
     *
     * @param protocol
     * @param resource
     * @param parameters
     * @return
     */
    public Joblet newJoblet( final String protocol, final String resource, final java.util.Map<String, String> parameters );
}
