package com.github.synesso.eshq

import java.net.URL
import dispatch.{Http, as, url, Req}
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write


class EventSourceClient private[eshq] (val key: Key, val secret: Secret,
  val serviceURL: URL = EventSourceClient.defaultURL,
  requestBuilder: (String, Map[String, String], Credentials) => Req,
  httpRequestor: (Req) => Future[String],
  jsonSerialiser: (AnyRef) => String) {

  /**
   * Opens a channel connection.
   * @param channel the identifier of the channel
   * @return On success, JSON containing the channel data required by the ESHQ JS client library.
   *         (See http://app.eventsourcehq.com/es.js)
   *         E.G. {presence_id: null, channel: "given-channel-name", socket: "abadd00d-cafe-face-b408-5854230f7a5b"}
   */
  def open(channel: Channel) = sendToService("/socket", channel.name, None)

  /**
   * Sends an event message to the given channel.
   * @param channel the channel name
   * @param event the event to send
   * @return On success, JSON containing an empty object "{}"
   */
  def send(channel: Channel, event: String): Future[String] = sendToService("/event", channel.name, Some(event))

  /**
   * Sends an event message to the given channel as JSON.
   * @param channel the channel name
   * @param event the event to send
   * @return On success, JSON containing an empty object "{}"
   */
  def sendJson(channel: Channel, event: AnyRef): Future[String] = send(channel, jsonSerialiser(event))

  private def sendToService(endpoint: String, channelName: String, event: Option[String]): Future[String] = {
    val credentials = Credentials(key, secret)
    val params = event.map(e => Map("data" -> e)).getOrElse(Map.empty[String, String]).updated("channel", channelName)
    val request = requestBuilder(s"$serviceURL$endpoint", params, credentials)
    httpRequestor(request)
  }
}

object EventSourceClient {
  def apply(key: Key, secret: Secret) = new EventSourceClient(key, secret, defaultURL, requestBuilder, httpRequestor,
    jsonSerialiser)

  def apply(key: Key, secret: Secret, serviceURL: URL) =
    new EventSourceClient(key, secret, serviceURL, requestBuilder, httpRequestor, jsonSerialiser)


  val defaultURL = new URL("http://app.eventsourcehq.com")

  private[eshq] val requestBuilder = (name: String, params: Map[String, String], credentials: Credentials) =>
    url(name) << params << credentials.asMap

  private[eshq] val httpRequestor = (request: Req) => Http(request OK as.String)

  implicit val formats: Formats = Serialization.formats(NoTypeHints)
  private[eshq] val jsonSerialiser = (any: AnyRef) => write(any)

}
