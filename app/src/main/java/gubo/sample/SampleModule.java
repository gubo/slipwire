
package gubo.sample;

import javax.inject.*;

import dagger.*;

import gubo.slipwire.*;

@Module
class SampleModule
{
    /*
     * will be called to satisfy field/constructor injection
     */
    @Provides @Singleton EventBus provideEventBus() {
        DBG.m( "SampleModule.provideEventBus" );
        return new EventBus();
    }

    /*
     * will be called to satisfy field/constructor injection
     */
    @Provides @Singleton DataBus provideDataBus() {
        DBG.m( "SampleModule.provideDataBus" );
        return new DataBus();
    }

    /*
     * will be called to satisfy field/constructor injection
     */
    @Provides Manageable provideManageable( final HomeManager manageable ) {
        DBG.m( "SampleModule.provideManageable" );
        return manageable;
    }
}
