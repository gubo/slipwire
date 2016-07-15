
package gubo.slipwire;

import android.support.annotation.*;

/**
 *
 */
public interface Presenter<D extends Display>
{
    /**
     *
     * @param d
     */
    public void bind( @Nullable D d );

    /**
     *
     */
    public void release();
}
