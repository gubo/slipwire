# slipwire
A strongly decoupled architecture for android applications, based on RxJava.

***Slipwire*** is framework for building Android applications, which, following the MVP pattern, aims to strictly decouple Android View instances from application flow and logic. To achieve this, Slipwire introduces a second bus, the DataBus, and makes an abstraction on the data flow between a Presenter and it's Display interface - the Presenter represents a DataSource and the Presenter.Display interface represents a DataSink. Thus, we can have only the Presenter.Display (an adapter) that is loosely coupled to the Android View instance.

##### DEPENDENCIES <br>
compile 'io.reactivex:rxandroid:1.0.1'          [LICENSE](https://github.com/ReactiveX/RxAndroid) <br>
compile 'io.reactivex:rxjava:1.0.14'            [LICENSE](https://github.com/ReactiveX/RxJava) <br>
compile 'com.squareup.retrofit:retrofit:1.9.0'  [LICENSE](http://square.github.io/retrofit/) <br>
