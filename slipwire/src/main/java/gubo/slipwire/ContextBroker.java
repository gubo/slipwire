
package gubo.slipwire;

import java.lang.ref.*;

/**
 * Implementations must be thread-safe.<br>
 * Should implement this on <A extends Application> class and use
 * Application.ActivityLifecycleCallbacks.onActivityStarted to create a WeakReference to Activity context.
 */
public interface ContextBroker
{
    /*
     * http://developer.android.com/reference/java/lang/ref/WeakReference.html
     * http://android-developers.blogspot.com/2009/01/avoiding-memory-leaks.html
     * http://stackoverflow.com/questions/3243215/how-to-use-weakreference-in-java-and-android-development
     * https://weblogs.java.net/blog/2006/05/04/understanding-weak-references
     * https://github.com/square/leakcanary
     */

    /**
     * Weak reference, because dont want to subvert lifecycle ... garbage collector should be able to claim an Activity
     * regardless if ContextBroker has previously returned a weak reference to it.
     * @return WeakReference to Application context
     */
    public WeakReference<android.content.Context> getApplicationContextReference();

    /**
     * Weak reference, because dont want to subvert lifecycle ... garbage collector should be able to claim an Activity
     * regardless if ContextBroker has previously returned a weak reference to it.
     * @return WeakReference to Activity context
     */
    public WeakReference<android.content.Context> getActivityContextReference();
}
