package com.github.synesso.eshq

import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import java.net.URL
import java.util.Date
import dispatch._

class Channel(val name: String, key: Key, secret: Secret, serviceURL: URL = EventSourceClient.defaultURL) {

  private val openResult = send("/socket", name, None)

  def send(event: String): Future[String] = {
    openResult flatMap {r =>
      send("/event", name, Some(event))
    }
  }

  private def send(endpoint: String, channelName: String, event: Option[String]): Future[String] = {
    val nowAsLong = new Date().getTime / 1000
    val token = new Token(key, secret, nowAsLong).asHexString
    val credentials = Map("key" -> key.value, "timestamp" -> s"$nowAsLong", "token" -> token)
    val params = event.map(e => Map("data" -> e)).getOrElse(Map.empty[String, String]).updated("channel", channelName)
    val request = url(s"$serviceURL$endpoint") << params << credentials
    Http(request OK as.String)
  }

}
