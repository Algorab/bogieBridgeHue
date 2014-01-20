package de.lauer_online.bogieBridgeHue.connector.leapMotion

import com.leapmotion.leap.{Listener, Controller}


/**
 * Creates an the LeapMotion Controller to add
 * the listeners later.
 */
object LeapMotionController {
 val controller = new Controller()
}

/**
 * Mixin to easy add and remove listeners to the LeapMotionController.
 */
trait LeapMotionController {

  lazy val controller = LeapMotionController.controller
  def addListener[T <: LeapActorContext with Listener](listener: LeapMotionListener[T]) = controller.addListener(listener: LeapMotionListener[T])
  def removeListener[T <: LeapActorContext with Listener](listener: LeapMotionListener[T]) = controller.removeListener(listener: LeapMotionListener[T])

}
