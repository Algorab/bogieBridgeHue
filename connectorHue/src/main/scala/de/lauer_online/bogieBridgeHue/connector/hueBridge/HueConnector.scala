package de.lauer_online.bogieBridgeHue.connector.hueBridge

import javax.ws.rs.core.MediaType
import de.lauer_online.bogieBridgeHue.connector.hueBridge.config.ConnectorHueConfig
import akka.actor.{Props, PoisonPill, Actor}
import de.lauer_online.bogieBridgeHue.connector.hueBridge.HueConnector._
import de.lauer_online.bogieBridgeHue.core.util.JerseyClient.client
import de.lauer_online.bogieBridgeHue.connector.hueBridge.HueConnector.HueExecute
import de.lauer_online.bogieBridgeHue.connector.hueBridge.HueConnector.BulbState
import com.fasterxml.jackson.databind.JsonNode
import com.sun.jersey.api.client.ClientResponse


/**
 * Connector for the Philips Hue Bridge.
 * Receive currently messages from connectorLeapMotion and connectorNetatmo
 */
object HueConnector {
  object BulbState {
    def apply(id: String,stateNode: JsonNode): BulbState = {
      BulbState(
        id.toInt,
        stateNode.findPath("on").asBoolean(),
        stateNode.findPath("sat").asInt(),
        stateNode.findPath("brie").asInt(),
        stateNode.findPath("hue").asInt()
      )
    }
  }
  case class BulbState(@transient bulbId: Int, on: Boolean, sat: Int, bri:Int, hue: Int)
  /* akka messages */
  case object GetBulbStates
  case object Stop
  case class HueExecute(state: BulbState)
  val name = "connectorHue"
  def props: Props = Props(classOf[HueConnector], name)
}
class HueConnector(name: String) extends ConnectorHueConfig with Actor {

  var currentStates = Map.empty[Int, BulbState]

  val BRIDGE_URL ="http://" + bridge + "/api/" + username
  val RESOURCE = "/lights/"
  val LIGHTS_URL = BRIDGE_URL + RESOURCE

  implicit val contentType = MediaType.APPLICATION_JSON

  override def preStart = {
    case class Bulb(name: String)
    val lightIds = client(LIGHTS_URL).get(classOf[Map[String, Bulb]]).keys

    currentStates = lightIds.foldLeft(Map.empty[Int, BulbState]) {
      (accu, id) =>
      val stateNode = client(LIGHTS_URL + id).get(classOf[JsonNode]).findPath("state")

      val bulbstate = BulbState(id, stateNode)

      if(stateNode.findPath("reachable").asBoolean())
        accu + ((bulbstate.bulbId, bulbstate))
      else
        accu
    }
  }

  def receive = {

    case HueExecute(bulbState: BulbState) =>
      val HUE_URL = LIGHTS_URL + bulbState.bulbId + "/state"

      currentStates += ((bulbState.bulbId, bulbState))
      client(HUE_URL).put(classOf[ClientResponse], bulbState)
      Thread.sleep(180)
    case GetBulbStates =>
      val se = sender
      sender ! currentStates
    case Stop => self ! PoisonPill

    }



}
