
package gubo.slipwire;

import rx.*;
import rx.subjects.*;

/**
 *
 */
public class EventBus
{
    /*
     * @see https://github.com/kaushikgopal/RxJava-Android-Samples
     * @see http://nerds.weddingpartyapp.com/tech/2014/12/24/implementing-an-event-bus-with-rxjava-rxbus/
     */

    private final Subject<Event,Event> subject = PublishSubject.create();
    private final SerializedSubject<Event,Event> serializedsubject = new SerializedSubject( subject );

    public void send( final Event event ) {
        serializedsubject.onNext( event );
    }

    public Observable<Event> toObserverable() {
        return serializedsubject;
    }
}
