# slipwire
A strongly decoupled architecture for android applications, based on RxJava.

***Slipwire*** is an MVP patterned framework for building Android applications, where a view is decoupled from the application logic and flow-control - views are only aware of a DataSource interface, and a type `<? extends Data>` that it is able to visualize. It utilises an EventBus and a DataBus, where a view is injected with a DataSource, and the Presenter.Display interface acts as a DataSink. The introduction of a DataBus, along with the DataSource interface, allow the Presenter to mediate (provide,cache) the data accessible to a view. As such, a view can 'request' more data, but consumes data in a reactive manner.

*An adapter class that implements a Presenter.Display interface may exist and be coupled to a view, but the view (and any backing-store adpater) itself need not be subclassed to contain any reactive code.*

<img src="https://docs.google.com/drawings/d/1k1kYMa2RuOlPbSxPCuSGIr2_Aa_GZToKcL8CRTUJ0i8/pub?w=960&amp;h=720">

In ***slipwire***, tasks are treated the same as network fetches. A local HTTP server runs and provides a servlet-like conatiner
to run jobs (joblets). An action class mediates the execution of a job. For example, an event may result in an action that subscribes to an observable over an http://localhost:* call. This results in the execution of a joblet. Perhaps this joblet broadcasts an intent for image selection from users gallery. Treating localized work in this asynchronous way leads to modularization and decoupling of the components of an application.

##### DEPENDENCIES <br>
compile 'io.reactivex:rxandroid:1.0.1'          [LICENSE](https://github.com/ReactiveX/RxAndroid) <br>
compile 'io.reactivex:rxjava:1.0.14'            [LICENSE](https://github.com/ReactiveX/RxJava) <br>
compile 'com.squareup.retrofit:retrofit:1.9.0'  [LICENSE](http://square.github.io/retrofit/) <br>
