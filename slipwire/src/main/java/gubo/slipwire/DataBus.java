
package gubo.slipwire;

import rx.*;
import rx.subjects.*;

/**
 *
 */
public class DataBus
{
    /*
     * @see https://github.com/kaushikgopal/RxJava-Android-Samples
     * @see http://nerds.weddingpartyapp.com/tech/2014/12/24/implementing-an-event-bus-with-rxjava-rxbus/
     */

    private final Subject<Data,Data> subject = PublishSubject.create();
    private final SerializedSubject<Data,Data> serializedsubject = new SerializedSubject( subject );

    /**
     *
     */
    public DataBus() {}

    /**
     *
     * @param data
     */
    public void send( final Data data ) {
        serializedsubject.onNext( data );
    }

    /**
     *
     * @return
     */
    public Observable<Data> toObserverable() {
        return serializedsubject;
    }
}
