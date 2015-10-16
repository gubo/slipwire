
package gubo.sample;

import javax.inject.*;

import dagger.*;

@Singleton
@Component( modules = SampleModule.class )
interface SampleComponent
{
    void inject( SampleActivity sampleactivity );
    void inject( SampleFragment samplefragment );
}
