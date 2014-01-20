package de.lauer_online.bogieBridgeHue.connector.netatmo.config

import de.lauer_online.bogieBridgeHue.core.config.BogieBridgeConfig

/**
 * Load and provide the configuration for the netatmo connector
 */
trait NetatmoConnectorConfig extends BogieBridgeConfig {

  private[this] val netatmoConnectorConfig = bridgeConfig.getConfig("netatmoConnector")

  val apiUrl = netatmoConnectorConfig.getString("server_api_url")

  val oAuthConfig =  netatmoConnectorConfig.getConfig("oAuth2")
  val oAuthTokenUrl = oAuthConfig.getString("server_token_url")
  val grant_type = oAuthConfig.getString("grant_type")
  val clientId = oAuthConfig.getString("client_id")
  val clientSecret = oAuthConfig.getString("client_secret")
  val username = oAuthConfig.getString("username")
  val password = oAuthConfig.getString("password")


}
