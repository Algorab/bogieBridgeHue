package de.lauer_online.bogieBridgeHue.core.util

import com.sun.jersey.api.client.config.DefaultClientConfig
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.filter.LoggingFilter
import javax.ws.rs.core.MediaType

/**
 * Jersey Client for the REST requests.
 */
object JerseyClient {

  val cc = new DefaultClientConfig
  cc.getClasses.add(classOf[JacksonJsonProvider])
  cc.getClasses.add(classOf[ObjectMapperProvider])

  def client(path: String = "")(implicit contentType: String) = {
    val c = Client.create(cc)
    //c.addFilter(new LoggingFilter())
    c.resource(path)
      .`type`(contentType)
      .accept(MediaType.APPLICATION_JSON)
  }

}
