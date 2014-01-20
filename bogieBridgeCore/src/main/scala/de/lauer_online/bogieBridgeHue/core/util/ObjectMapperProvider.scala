package de.lauer_online.bogieBridgeHue.core.util

import javax.ws.rs.ext.{ContextResolver, Provider}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

/**
 * Load the DefaultScalaModule for jersey.
 */
@Provider
class ObjectMapperProvider extends ContextResolver[ObjectMapper] {

  override def getContext(`type`: Class[_]): ObjectMapper = {
    val om = new ObjectMapper()
    om.registerModule(DefaultScalaModule)
    om
  }
}