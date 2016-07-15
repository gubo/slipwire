# slipwire
A strongly decoupled architecture for android applications, based on RxJava.

***Slipwire*** is framework for building Android applications, which, following the MVP pattern, aims to strictly decouple Android View instances from application flow and logic. To achieve this, Slipwire introduces a second bus, the *DataBus*, and makes an abstraction on the data flow between a Presenter and it's Display interface. A Presenter represents a *DataSource* and it's Presenter.Display interface represents a *DataSink*. Thus, we can have that only the Presenter.Display is loosely coupled to the Android View instance. This promotes describing a View by an XML layout, and discourages extending View classes (for purposes other than data visualization). 

<br>
The following diagram depicts the Slipwire structure:
<img src="https://docs.google.com/drawings/d/1k1kYMa2RuOlPbSxPCuSGIr2_Aa_GZToKcL8CRTUJ0i8/pub?w=960&amp;h=720">
<br>
<br>

##### GETTING STARTED <br>

The core of Slipwire is in the gubo.slipwire package.

##### DOCUMENTATION <br>

*Coming Soon*

##### CONTRIBUTING <br>

*Coming Soon*

##### DEPENDENCIES <br>
com.android.support:support-annotations:23.3.0<br>
io.reactivex:rxandroid:1.0.1<br>
io.reactivex:rxjava:1.0.14<br>


##### LICENSE 
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
