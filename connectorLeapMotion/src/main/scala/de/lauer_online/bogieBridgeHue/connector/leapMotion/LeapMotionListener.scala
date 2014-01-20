package de.lauer_online.bogieBridgeHue.connector.leapMotion

import com.leapmotion.leap.{Controller, Listener}
import akka.actor.{ActorSelection, ActorContext, ActorRef, Actor}
import scala.reflect.ClassTag
import de.lauer_online.bogieBridgeHue.core.logging.Logging


/**
 * Listener delegate to have a uniform type.
 */
class LeapMotionListener[T <: LeapActorContext with Listener: ClassTag](actor: ActorRef) extends Listener with Logging {

  val listener = implicitly[ClassTag[T]].runtimeClass.getConstructor(classOf[ActorRef]).newInstance(actor).asInstanceOf[Listener]

  override def onInit (controller: Controller) = listener.onInit(controller)
  override def onConnect(controller: Controller) = listener.onConnect(controller)
  override def onDisconnect(controller: Controller) = listener.onDisconnect(controller)
  override def onExit(controller: Controller) = listener.onExit(controller)
  override def onFrame(controller: Controller) = listener.onFrame(controller)

}
