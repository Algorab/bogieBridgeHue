package de.lauer_online.bogieBridgeHue.connector.leapMotion

import com.leapmotion.leap._
import akka.actor._
import de.lauer_online.bogieBridgeHue.connector.hueBridge.HueConnector
import de.lauer_online.bogieBridgeHue.connector.leapMotion.HueHandlerActor.GetBulbStates
import com.leapmotion.leap.Gesture.Type._
import akka.pattern._
import scala.concurrent._
import duration._
import akka.util.Timeout
import de.lauer_online.bogieBridgeHue.connector.hueBridge.HueConnector.BulbState
import de.lauer_online.bogieBridgeHue.connector.leapMotion.HueHandlerActor.SendBulbState
import de.lauer_online.bogieBridgeHue.core.logging.{Logging, Logger}

/**
 * Handler implements the LeapMotionListener. Gets an ActorRef in this case
 * the HueHandlerActor.
 */
class HueHandler(val actor: ActorRef) extends LeapActorContext with Logging {

  var bulbStates = Map.empty[Int, BulbState]
  var bulbsUnderCtrl = List.empty[Int]

  //bulbStates = Map(1 -> BulbState(1, true, 255,255,27500), 2 -> BulbState(2, false, 255,255,27500))
  implicit val timeout = Timeout(5 seconds)

  private [this] def switchBulb(bulbId: Int, dirCalc: Int => Int) = {
    val bulbList = bulbStates.keys.toList.sorted
    val currBulbIndex = bulbList.indexOf(bulbId)

    dirCalc(currBulbIndex) match {
      case x if(x >= bulbList.length) =>
        bulbsUnderCtrl = List(bulbList(0))
      case x if(x < 0) =>
        bulbsUnderCtrl = List(bulbList.last)
      case x =>
        bulbsUnderCtrl = List(bulbList(x))
    }


  }

  def checkBulbStates = bulbStates.forall(p => p._1 == p._2.bulbId)

  override def onInit (controller: Controller) = {
    bulbStates = Await.result((actor ? GetBulbStates).mapTo[Map[Int, BulbState]], 20 seconds)
    //ToDo: Check double toList
    bulbsUnderCtrl = bulbStates.keys.toList.sorted.toList
  }
  override def onConnect(controller: Controller) = {
    //logger.debug("Listener Connected")
    controller.enableGesture(Gesture.Type.TYPE_SWIPE)
    controller.enableGesture(Gesture.Type.TYPE_CIRCLE)
    //controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP)
    controller.enableGesture(Gesture.Type.TYPE_KEY_TAP)
  }
  override def onDisconnect(controller: Controller) = {
    //logger.debug("Listener Disconnected")
  }
  override def onExit(controller: Controller) = {
    //logger.debug("Listener Exited")
  }
  override def onFrame(controller: Controller) = {
    //logger.debug("Listener onFrame")
    //println("Listener onFrame " + count)

    import scala.collection.JavaConversions._

    val state = bulbStates.get(bulbsUnderCtrl.head).get

    val gestures = controller.frame.gestures
      gestures.foreach {
        gesture => gesture.`type` match {

          //Change the color of a bulb
          case TYPE_CIRCLE =>
            if(state.on) {
              val previousCircle = new CircleGesture(controller.frame(1).gesture(gesture.id))

              val circle = new CircleGesture(gesture)
              val circleAmount = circle.progress.toInt

              if (previousCircle.progress.toInt != circleAmount) {

                val hue =
                  if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI/4)
                    Clockwise(state.hue, circleAmount).newHue
                  else
                    CounterClockwise(state.hue, circleAmount).newHue


                  val newState = state.copy(hue = hue, sat=255, bri=255)
                  bulbStates += ((newState.bulbId, newState))
                  actor ! HueHandlerActor.SendBulbState(newState)
              }
            }

          //set bulb off light
          case TYPE_KEY_TAP =>
            val state = bulbStates.get(bulbsUnderCtrl.head).get
            val newState = state.copy(on = !state.on)
            bulbStates += ((newState.bulbId, newState))
            actor ! HueHandlerActor.SendBulbState(newState)

          //activate the next bulb
          case TYPE_SWIPE =>
            val swipe = new SwipeGesture(gesture)

            if(swipe.state == Gesture.State.STATE_STOP) {

              /* only swipe on xAxis */
              if(Math.abs(swipe.direction.getX) > 0.75 && controller.frame.hands.leftmost.fingers.size == 1) {

                val direction = if(swipe.direction.getX > 0) SwipeRight else SwipeLeft

                switchBulb(bulbsUnderCtrl.head, direction.swipe)

                val swipeState = bulbStates.get(bulbsUnderCtrl.head).get

                /* blink the active light */
                actor ! HueHandlerActor.SendBulbState(BulbState(bulbsUnderCtrl.head, true, 255, 255, 25000))
                Thread.sleep(300)
                actor ! HueHandlerActor.SendBulbState(BulbState(bulbsUnderCtrl.head, true, 0, 255, 25000))
                Thread.sleep(300)
                actor ! HueHandlerActor.SendBulbState(BulbState(bulbsUnderCtrl.head, true, 255, 255, 25000))
                Thread.sleep(300)

                if(!swipeState.on) {
                  val newState = state.copy(on = true)
                  bulbStates += ((state.bulbId, newState))
                  actor ! HueHandlerActor.SendBulbState(newState)
                } else {
                  actor ! HueHandlerActor.SendBulbState(swipeState)
                }
              }
            }
          case gesture => logger.debug("not implemented gesture: " + gesture)
        }
      }

    /*
      x-direction for saturation (white amount in the color)
      y-direction for brightness
     */
    if(gestures.size == 0 && state.on && controller.frame.hands.leftmost.fingers.size == 5) {

      val direction = controller.frame().hands().leftmost()


      def rangeCheck(value: Int) = {
        value match {
          case value if(value < 0 ) => 0
          case value if(value > 255) => 255
          case value => value
        }
      }


      val sat = rangeCheck(direction.stabilizedPalmPosition().getX.toInt + 127)
      val bri = rangeCheck(direction.stabilizedPalmPosition().getY.toInt - 50)

      val newState = state.copy(sat=sat, bri=bri)
      bulbStates += ((bulbsUnderCtrl.head, newState))
      actor ! HueHandlerActor.SendBulbState(newState)
      Thread.sleep(500)


    }
  }

}

/**
 * Actor for the HueHandler which implements the LeapMotionListener.
 * sends the message to the actor to set the state for the bulb.
 */
object HueHandlerActor {

  case class SendBulbState(bulbState: BulbState)
  case object GetBulbStates

  val name = "hueHandlerActor"
  def props: Props = Props(classOf[HueHandlerActor], name)
}
class HueHandlerActor(name: String) extends Actor {
  val hueConnector =  context.system.actorSelection("/user/" + HueConnector.name)

  implicit val timeout = Timeout(5 seconds)

  def receive = {
    case SendBulbState(bulbState) => hueConnector ! HueConnector.HueExecute(bulbState)
    case GetBulbStates =>
      sender ! Await.result(( hueConnector ? HueConnector.GetBulbStates).mapTo[Map[Int, BulbState]], 10 seconds)
  }
}

trait CircleDirection {
  def rangeCheck(hue: Int) = {
    hue match {
      case x if(x > 65535) => 1
      case x if(x < 0) => 65535
      case x => x
    }
  }
}
case class Clockwise(oldHue: Int, circleAmount: Int) extends CircleDirection {
  def newHue = rangeCheck(oldHue + (circleAmount * 1000))
}
case class CounterClockwise(oldHue: Int, circleAmount: Int) extends CircleDirection {
  def newHue = rangeCheck(oldHue - (circleAmount * 1000))
}

trait SwipeDirection {
  def swipe: Int => Int
}
case object SwipeLeft extends SwipeDirection {
  def swipe = {
    index:Int => index - 1
  }
}
case object SwipeRight extends SwipeDirection {
  def swipe = {
    index: Int => index + 1
  }
}



