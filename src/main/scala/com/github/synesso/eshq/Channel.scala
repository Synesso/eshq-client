package com.github.synesso.eshq

import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import java.net.URL
import dispatch._

private[eshq] class Channel(val name: String, key: Key, secret: Secret, serviceURL: URL = EventSourceClient.defaultURL,
  requestBuilder: (String, Map[String, String], Credentials) => Req = Channel.requestBuilder,
  httpRequestor: (Req) => Future[String] = Channel.httpRequestor) {

  private val openResult = send("/socket", name, None)

  def send(event: String): Future[String] = {
    // todo - map and wrap, so that the exception is known from the open or send command.
    openResult flatMap {_ => send("/event", name, Some(event))}
  }

  private def send(endpoint: String, channelName: String, event: Option[String]): Future[String] = {
    val credentials = Credentials(key, secret)
    val params = event.map(e => Map("data" -> e)).getOrElse(Map.empty[String, String]).updated("channel", channelName)
    val request = requestBuilder(s"$serviceURL$endpoint", params, credentials)
    httpRequestor(request)
  }

}

object Channel {
  def apply(name: String, key: Key, secret: Secret) = new Channel(name, key, secret)
  def apply(name: String, key: Key, secret: Secret, serviceURL: URL) = new Channel(name, key, secret, serviceURL)

  private[eshq] val requestBuilder = (name: String, params: Map[String, String], credentials: Credentials) =>
    url(name) << params << credentials.asMap
  private[eshq] val httpRequestor = (request: Req) => Http(request OK as.String)
}
