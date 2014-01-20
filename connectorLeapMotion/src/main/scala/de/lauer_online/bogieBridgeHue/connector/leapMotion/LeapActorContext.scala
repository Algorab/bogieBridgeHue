package de.lauer_online.bogieBridgeHue.connector.leapMotion

import com.leapmotion.leap.Listener
import akka.actor.{ActorSelection, ActorContext, Actor, ActorRef}


/**
 * Interface for a listener implementation
 */
trait LeapActorContext extends Listener {
  val actor: ActorRef
}
