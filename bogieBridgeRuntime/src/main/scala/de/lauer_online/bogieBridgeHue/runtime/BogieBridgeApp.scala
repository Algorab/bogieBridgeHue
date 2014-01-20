package de.lauer_online.bogieBridgeHue.runtime

import akka.actor._
import de.lauer_online.bogieBridgeHue.connector.hueBridge.HueConnector
import de.lauer_online.bogieBridgeHue.core.config.BogieBridgeConfig
import de.lauer_online.bogieBridgeHue.core.logging.Logging
import akka.pattern.gracefulStop
import scala.concurrent.duration._
import de.lauer_online.bogieBridgeHue.connector.leapMotion.LeapMotionConnector


/**
 * Base class of the runtime. Main class, start the akka system. And the connectors.
 */
object BogieBridgeApp extends App with BogieBridgeConfig with Logging {

  val actorSystem = ActorSystem("hueBogieBridge", bridgeConfig.getConfig("bogieBridgeApp"))

  val bogieBridgeController = actorSystem.actorOf(BogieBridgeController.props, BogieBridgeController.name)

  import BogieBridgeController._

  object BogieBridgeController {
    case object Start
    case object Stop
    case object Run
    val name = "bogieBridgeController"
    def props: Props = Props(classOf[BogieBridgeController], name)
  }
  class BogieBridgeController(name: String) extends Actor with ActorLogging {

    def receive = {
      case Start =>
        log.debug("Receive Start")

        /* create the needed connector actors */
        val connectorHue = actorSystem.actorOf(HueConnector.props, HueConnector.name)
        //val connectorNetAtmo = actorSystem.actorOf(NetatmoConnector.props, NetatmoConnector.name)
        val connectorLeapMotion = actorSystem.actorOf(LeapMotionConnector.props, LeapMotionConnector.name)

        /* set bulb color depending of the outdoor temperature */
        //connectorNetAtmo ! NetatmoConnector.NetatmoExecute

      case Stop =>
        log.debug("Receive Controller Stop")

        /* kill the the actors */
        //connectorNetAtmo ! NetatmoConnector.Stop
        //connectorHue ! HueConnector.Stop
        gracefulStop(self, 5 seconds)
        actorSystem.shutdown
        System.exit(0)
    }
  }

  bogieBridgeController ! Start

}