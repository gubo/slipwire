
package gubo.slipwire;

import java.util.*;

/**
 *
 */
public interface Joblet
{
    /**
     *
     * @param contextbroker
     */
    public void setContextBroker( ContextBroker contextbroker );

    /**
     *
     * @param parameters
     */
    public void setParameters( final Map<String,String> parameters );

    /**
     * Perform a job off the main ui thread.<br>
     *
     * @return a JSON result string
     */
    public String perform();
}
