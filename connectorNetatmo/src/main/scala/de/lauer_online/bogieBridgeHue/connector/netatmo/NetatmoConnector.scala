package de.lauer_online.bogieBridgeHue.connector.netatmo

import com.sun.jersey.api.representation.Form
import java.net.URLEncoder
import javax.ws.rs.core.MediaType
import com.fasterxml.jackson.databind.JsonNode
import de.lauer_online.bogieBridgeHue.core.util.JerseyClient
import JerseyClient._
import de.lauer_online.bogieBridgeHue.connector.netatmo.config.NetatmoConnectorConfig
import akka.actor.{PoisonPill, Props, Actor}
import de.lauer_online.bogieBridgeHue.connector.netatmo.NetatmoConnector.{NetatmoExecute, Stop}
import de.lauer_online.bogieBridgeHue.connector.hueBridge.{TempToColor, HueConnector}
import de.lauer_online.bogieBridgeHue.connector.hueBridge.HueConnector.BulbState

/**
 * Factory for the NetatmoConnector Actor
 */
object NetatmoConnector {
  case object NetatmoExecute
  case object Stop
  val name = "connectorNetatmo"
  def props: Props = Props(classOf[NetatmoConnector], name)
}
/**
 * Connector to the netatmo data cloud. Request the outdoor temperature, and
 * the the color of the bulb depending on it.
 * @param name
 */
class NetatmoConnector(name: String) extends NetatmoConnectorConfig with Actor {

  val connectorHue = context.actorSelection("/user/" + HueConnector.name)

  def receive = {
    case NetatmoExecute =>

    /*
    Download signing certificate of StartCom Class 2 Primary with:
    wget http://www.startssl.com/certs/sub.class2.server.ca.pem


    Create a trust store with command:
    keytool -import -file [cacert.pem] -alias CAAlias -keystore [truststore.ts] -storepass [StorePass]
   */

    System.setProperty("javax.net.ssl.trustStore","ssl/netatmo.ts")
    System.setProperty("javax.net.ssl.trustStorePassword", "netatmo")

    implicit val contentType = MediaType.APPLICATION_FORM_URLENCODED

    /* make this as implicit class */
    val tokenForm = new Form
    tokenForm.add("grant_type", grant_type)
    tokenForm.add("client_id", clientId)
    tokenForm.add("client_secret", clientSecret)
    tokenForm.add("username", username)
    tokenForm.add("password", password)

    val token = client(oAuthTokenUrl).post(classOf[NetatmoToken], tokenForm)

    val body = client(apiUrl + "devicelist?access_token="+ URLEncoder.encode(token.access_token, "UTF-8")).get(classOf[JsonNode])

    val lastData = body.findPath("last_data_store")

    val outDoorTemp = lastData.get("02:00:00:00:51:52").get("a").asInt

    Range(-20, 40).toList.foreach {
      temp  =>
        val bulbState = BulbState(1, true, 255, 255, TempToColor.getColorValue(temp))
        connectorHue ! HueConnector.HueExecute(bulbState)
        //connectorHue ! HueConnector.HueExecute(BulbState(1, true, 255, 255, TempToColor.getColorValue(outDoorTemp)))
        Thread.sleep(200)
    }

    case Stop => self ! PoisonPill

  }

}

/**
 * Class to to deserialize the request response for a token,
 * to fetch late the measured data
 * @param access_token
 * @param refresh_token
 * @param expires_in
 * @param expire_in
 * @param scope
 */
case class NetatmoToken(access_token: String, refresh_token: String, expires_in: Int, expire_in: Int, scope: String)
