package de.lauer_online.bogieBridgeHue.core.config

import com.typesafe.config.ConfigFactory

/**
 * load the configuration and provide it as a trait.
 */
object BogieBridgeConfig {
  val bridgeConfig = ConfigFactory.load.getConfig("hueBogieBridge")
}
trait BogieBridgeConfig {
  lazy val bridgeConfig = BogieBridgeConfig.bridgeConfig
}
