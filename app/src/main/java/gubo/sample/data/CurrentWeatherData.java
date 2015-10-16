
package gubo.sample.data;

import gubo.slipwire.*;

public class CurrentWeatherData extends Data
{
    public int id;
    public float temp;
    public int humidity;
    public int pressure;
    public String heading;
    public String iconurl;

    public CurrentWeatherData( final Object origin ) {
        super( origin );
    }
}
