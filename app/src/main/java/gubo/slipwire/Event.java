
package gubo.slipwire;

import java.util.*;

import android.view.*;

/**
 *
 */
public abstract class Event
{
    /**
     *
     * @param view
     * @return
     */
    public static final Event newEvent( final Package _package, final View view ) {
        Event uievent = null;
        String classname = null;
        try {
            final Object _tag = view.getTag();
            if ( _tag instanceof String ) {
                classname = _package.getName() + "." + ( String)_tag;
                final Object object = Class.forName( classname ).newInstance();
                if ( object instanceof Event ) {
                    uievent = ( Event )object;
                }
            }
        } catch ( ClassNotFoundException x ) {
            DBG.w( "NOT FOUND " + classname );
        } catch ( Exception x ) {
            DBG.m( x );
        }
        return uievent;
    }

    private static final Map<Class<? extends Event>,Class<? extends Action>> actionmap = new HashMap<>();

    public static void setAction( final Class<? extends Event> event, final Class<? extends Action> action ) {
        synchronized ( actionmap ) {
            if ( event != null ) {
                actionmap.put( event,action );
            }
        }
    }

    public static Class<? extends Action> getAction( final Class<? extends Event> event ) {
        Class<? extends Action> action = null;
        synchronized ( actionmap ) {
            if ( event != null ) {
                action = actionmap.get( event );
            }
        }
        return action;
    }

    public static void rmvAction( final Class<? extends Event> event, final Class<? extends Action> action ) {
        synchronized ( actionmap ) {
            if ( event != null ) {
                actionmap.remove( event );
            }
        }
    }

    public static void clearActions() {
        synchronized ( actionmap ) {
            actionmap.clear();
        }
    }
}
