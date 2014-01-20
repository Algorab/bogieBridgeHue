Connector LeapMotion
=================================================

Connector LeapMotion handle the LeapMotion-Controller messages. To fetch the messages
the controller need a listener implementation. The Listener is an actor assigned to communicate
with the connectorHue.


Setup Developement Enviroment for IntelliJ
------------------------------------------

* Get LeapMotion SDK from the [LeapMotion-Developer-Site](https://www.leapmotion.com/developers)
* Copy the LeapJava.jar into the lib directory for unmanaged sbt dependencies.
* Add ```-Djava.library.path=[YOUR_PATH]/LeapDeveloperKit/LeapSDK/lib``` to the run configuration.


Supported Gestures
------------------------------------------

* circle => change the color

* palmPosition with 5 finger spread
    * xAxis => change the saturation
    * yAxis => change the brightness

* swipe with one finger
    * xAxis => change the bulb under control. New active bulb will flash green - white green

