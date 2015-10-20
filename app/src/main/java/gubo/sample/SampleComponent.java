
package gubo.sample;

import javax.inject.*;

import dagger.*;

@Singleton
@Component( modules = SampleModule.class )
interface SampleComponent
{
    void inject( HomeActivity homeactivity );
    void inject( HomeFragment homefragment );

    void inject( BooksActivity booksactivity );
    void inject( BooksFragment booksfragment );
}
