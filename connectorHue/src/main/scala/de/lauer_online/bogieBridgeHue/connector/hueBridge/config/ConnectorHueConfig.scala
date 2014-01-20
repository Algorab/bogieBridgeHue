package de.lauer_online.bogieBridgeHue.connector.hueBridge.config

import de.lauer_online.bogieBridgeHue.core.config.BogieBridgeConfig

/**
 * Load and provide the configuration which is needed for the
 * Philips Hue Bridge
 */
trait ConnectorHueConfig extends BogieBridgeConfig {

  private[this] val hueConnectorConfig = bridgeConfig.getConfig("hueConnector")

  val bridge = hueConnectorConfig.getString("bridge")
  val username = hueConnectorConfig.getString("username")



}
