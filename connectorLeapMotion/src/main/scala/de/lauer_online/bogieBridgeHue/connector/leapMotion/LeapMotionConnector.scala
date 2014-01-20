package de.lauer_online.bogieBridgeHue.connector.leapMotion

import akka.actor.{ActorRef, Props, Actor}
import de.lauer_online.bogieBridgeHue.connector.hueBridge.HueConnector
import de.lauer_online.bogieBridgeHue.core.logging.Logging


/**
 * Factory for the LeapMotionConnector Actor
 */
object LeapMotionConnector {
  case object Start
  case object Stop

  val name = "connectorLeapMotion"

  def props: Props = Props(classOf[LeapMotionConnector], name)
}
/**
 * Connector for the LeapMotionController.
 * Add the LeapMotionListener implementation the LeapMotionController.
 */
class LeapMotionConnector(name: String) extends Actor with Logging with LeapMotionController {

  val handlerActor = context.actorOf(HueHandlerActor.props, HueHandlerActor.name)

  val listener = new LeapMotionListener[HueHandler](handlerActor)
  controller.addListener(listener)

  def receive = {
    case _ => logger.error("Receive Messages not support yet.")
  }
}
