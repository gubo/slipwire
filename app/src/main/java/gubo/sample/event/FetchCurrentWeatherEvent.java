
package gubo.sample.event;

import gubo.slipwire.*;

public class FetchCurrentWeatherEvent extends Event
{
    public FetchCurrentWeatherEvent( final Object origin ) {
        super( origin );
    }
}
