
package gubo.slipwire;

import java.lang.ref.*;

import android.content.*;

/**
 *
 */
public interface ContextBroker
{
    /**
     * Thread-safe.
     * @return WeakReference to Application context
     */
    public WeakReference<Context> getApplicationContext();

    /**
     * Thread-safe.
     * @return WeakReference to Activity context
     */
    public WeakReference<Context> getActivityContext();
}
