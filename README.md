# slipwire
A strongly decoupled architecture for android applications, based on RxJava.

***Slipwire*** is framework for building Android applications, which, following the MVP pattern, aims to strictly decouple Android View instances from application flow and logic. To achieve this, Slipwire introduces a second bus, the *DataBus*, and makes an abstraction on the data flow between a Presenter and it's Display interface. A Presenter represents a *DataSource* and it's Presenter.Display interface represents a *DataSink*. Thus, we can have that only the Presenter.Display is loosely coupled to the Android View instance. This promotes describing a View by an XML layout, and discourages extending View classes (for purposes other than data visualization). <sub>A further level of abstraction could be introduced, whereby an Android View is extended to implement, say, a "Visualize" interface, and thus a Presenter.Display potentially need not know of any Android View types nor the "resource-id" mechanism, but this is not strictly necessary to achieve a loose coupling.</sub>

Apart from the de-coupling of Android Views, Slipwire also maintains separation of concerns by treating "localized" tasks, or *Action*s, as asynchronous bahaviours that are basically the equivalent (analagogues) of network or RPC request/response operations. So, we introduce the notion of a *Joblet*, in which a self-contained unit of execution can perform work on behalf of the application. A Joblet can be thought of as a Servlet that is not sandboxed within the Android runtime - in fact, it is an instance of an embedded Jetty server, with a "JobletHandler", that is the umbrella under which a Joblet is running. To the Slipwire application, network fetches and local jobs are treated in the same way - both are invoked as RXJava Observables wrapping (Retrofit/Volley) HTTP request/response sessions. The development of a Slipwire application can benefit from the concurrent development of Joblets, and Joblets lend themselves to mock unit testing.

So, in Slipwire, we have a de-coupling of components that act on the data which flows thru an application - MVP pattern, Joblets. Therefore, it is up to the *DataBus* to satisfy the data consumer needs of the de-coupled components. Both the *EventBus* and *DataBus* in Slipwire are implemented as RxJava Subjects, and any component can be an Observer on either bus or both. However, Slipwire encourages that an application follow some restrictions: 
- <sub>Presenter(s) should be the sole Observer(s) of *Data*</sub>
- <sub>A Manager should mediate the *Event*s that propogate *Action*s</sub>
- <sub>*Action*s should be the sole sender(s) of *Data*</sub> 

In addition to the above recommendations for data-flow, Slipwire encourages that:
- <sub>A Manager should bind/unbind View(s) to "Adapter"(s)</sub>
- <sub>Presenter(s) should be the sole sender of *Event*s that are a response to user input</sub>
- <sub>Presenter(s) should be the sole sender of *Event*s that propogate *Action*s which create *Data*</sub>

The following diagram depicts the Slipwire structure:
<img src="https://docs.google.com/drawings/d/1k1kYMa2RuOlPbSxPCuSGIr2_Aa_GZToKcL8CRTUJ0i8/pub?w=960&amp;h=720">

##### DEPENDENCIES <br>
compile 'com.google.code.gson:gson:2.3' <br>
compile project(':volley') <sub>*https://android.googlesource.com/platform/frameworks/volley*</sub> <br>
compile 'io.reactivex:rxandroid:1.0.1'              <sub>[LICENSE](https://github.com/ReactiveX/RxAndroid)</sub> <br>
compile 'io.reactivex:rxjava:1.0.14'                <sub>[LICENSE](https://github.com/ReactiveX/RxJava)</sub> <br>
compile 'com.squareup.retrofit:retrofit:1.9.0'      <sub>[LICENSE](http://square.github.io/retrofit/)</sub> <br>
compile 'org.eclipse.jetty:jetty-server:7.6.0.RC4'  <sub>[LICENSE](http://www.eclipse.org/jetty/licenses.php)</sub> <br>
compile 'org.eclipse.jetty:jetty-servlet:7.6.0.RC4' <sub>[LICENSE](http://www.eclipse.org/jetty/licenses.php)</sub> <br>
