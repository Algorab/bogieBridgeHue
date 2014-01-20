package de.lauer_online.bogieBridgeHue.control

import akka.actor._
import de.lauer_online.bogieBridgeHue.runtime.BogieBridgeApp.BogieBridgeController
import de.lauer_online.bogieBridgeHue.core.config.BogieBridgeConfig
import de.lauer_online.bogieBridgeHue.core.logging.Logging
import akka.pattern.gracefulStop
import scala.concurrent.duration._
import scala.Some
import akka.actor.Identify

/**
 * Controller for the Runtime.
 * At moment only stop supported.
 */
object BogieBridgeCtl extends App with BogieBridgeConfig with Logging {



  import scala.util.control.Exception._

  val actorSystem = ActorSystem("hueBogieBridgeCtl", bridgeConfig.getConfig("bogieBridgeCtl"))
  val remotePath = "akka.tcp://hueBogieBridge@127.0.0.1:2554/user/" + BogieBridgeController.name
  val bogieBridgeApp = actorSystem.actorSelection(remotePath)




  allCatch.opt(args(0).toUpperCase) match {
    case Some("START") =>
      logger.debug("Send Start")
      bogieBridgeApp ! BogieBridgeController.Start
    case Some("STOP")  =>
      logger.debug("Send Stop")
      bogieBridgeApp ! BogieBridgeController.Stop
    case _ => println("valid commands: start stop ")
  }
  Thread.sleep(500)
  actorSystem.shutdown
}
