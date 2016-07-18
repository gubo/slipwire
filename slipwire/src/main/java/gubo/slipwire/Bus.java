
package gubo.slipwire;

import rx.*;
import rx.schedulers.Schedulers;
import rx.subjects.*;

/**
 *
 * @param <T>
 */
public class Bus<T>
{
    /*
     * https://github.com/kaushikgopal/RxJava-Android-Samples
     * http://blog.human-readable.net/2015/08/lightweight-event-bus.html
     * http://nerds.weddingpartyapp.com/tech/2014/12/24/implementing-an-event-bus-with-rxjava-rxbus
     */

    private final Subject<T,T> subject = PublishSubject.create();
    private final SerializedSubject<T,T> serializedsubject = new SerializedSubject( subject );

    /**
     *
     */
    public Bus() {}

    /**
     *
     * @param e
     * @param <E>
     */
    public <E extends T> void send( final E e ) {
        if ( DBG.isTrace() ) { DBG.t( getClass().getSimpleName()+".send " + e ); }
        serializedsubject.onNext( e );
    }

    /**
     *
     * @return
     */
    public Observable<T> observe() {
        return serializedsubject;
    }

    /**
     *
     * @param c
     * @param <E>
     * @return
     */
    public <E extends T> Observable<E> observe( Class<E> c ) {
        return serializedsubject.ofType( c );
    }

    /**
     *
     * @return
     */
    public boolean isCold() {
        return !serializedsubject.hasObservers();
    }
}
