
package gubo.sample.view;

import android.util.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.annotation.*;

public class BusyView extends RelativeLayout
{
    public BusyView( final Context context ) { super( context ); }
    public BusyView( final Context context,final AttributeSet attributes ) { super( context,attributes ); }
    public BusyView( final Context context,final AttributeSet attributes,final int style ) { super( context,attributes,style ); }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent( final MotionEvent event ) {
        return true;
    }
}
