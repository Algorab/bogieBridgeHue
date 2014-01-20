Bogie Bridge Hue - Hue Bridge Connector Framework
=================================================


Initial Idea
--------------------------------------
The initial idea was to set the light color depending on the outdoor temperature coming from
my netatmo weather station.
Some friends of mine have this new, cool and fancy LeapMotion-Controller. The
next thing I thought it would be awesome, to set the light color, saturation and
brightness and so on by gestures. Then I start this little project, it is in a very early state.

During development I think how about to run this. A normal PC or Laptop is to big
and need to much energie. A look to different one board computers like [rasberry pi](http://www.raspberrypi.org),
[pandaboard](http://pandaboard.org), [beagleboard](http://beagleboard.org) let me decide to the [hardkernel](http://www.hardkernel.com).
Because, the LeapMotion-Controller should connected with a pc via USB3.0 and need a little bit of performance, but it
was not the cheapest one.


The Project
--------------------------------------
The project has two basic parts, the daemon and the control client part. Both parts communicate via akka remoting.

### The Daemon
The Daemon consists of several modules.

#### The Runtime
The runtime start tha akka system and the different connectors.

#### The Connectors
Each Connector implements an akka Actor which receive and/or send messages.

At moment there are three connectors available:

* Philips Hue
* Netatmo
* LeapMotion

### The Control Client
The Control Client controls the daemon.
At momment only Stop is supported.

### Used Technologies

* Philips Hue
* Netatmo
* scala + sbt + akka
* jersey + https

### Packageing

The packaging is done via the [sbt-pack plugin](https://github.com/xerial/sbt-pack).



