package com.github.synesso.eshq

import org.specs2.{ScalaCheck, Specification}
import scala.concurrent.{Await, ExecutionContext, Future}
import ExecutionContext.Implicits.global
import org.specs2.mock.Mockito
import java.net.ConnectException
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit._
import dispatch.Req
import dispatch.StatusCode
import java.util.Date

class ClientSpec extends Specification with Mockito with ScalaCheck with ArbitraryValues { def is = s2"""

  An EventSourceClient must
    send a request to /socket with provided channel name on open $sendToSocket
    send a string request to /event with channel and data on send $sendStringToEvent
    send an AnyRef to /event with channel and data on send $sendAnyRefToEvent
    report the open request error $openError
    report the open request failure $openFailure
    report the send request error $sendError
    report the send request failure $sendFailure
    work as documented in the readme $temperatureExample

"""

  val openRequest = Req(identity)
  val sendRequest = Req(identity)

  def sendToSocket = prop { (name: String, key: Key, secret: Secret) =>
    val (requestBuilder, httpRequestor, jsonSerialiser) = mocks

    requestBuilder(endWith("/socket"), ===(Map("channel" -> name)), any[Credentials]) returns openRequest
    httpRequestor(openRequest) returns Future("openOK")

    val client = new EventSourceClient(key, secret, requestBuilder = requestBuilder, httpRequestor = httpRequestor,
      jsonSerialiser = jsonSerialiser)
    client.open(Channel(name)) must beEqualTo("openOK").await
  }

  def sendStringToEvent = prop { (name: String, key: Key, secret: Secret, event: String) =>
    val (requestBuilder, httpRequestor, jsonSerialiser) = mocks

    requestBuilder(endWith("/event"), ===(Map("data" -> event, "channel" -> name)), any[Credentials]) returns sendRequest
    httpRequestor(sendRequest) returns Future("send OK")

    val client = new EventSourceClient(key, secret, requestBuilder = requestBuilder, httpRequestor = httpRequestor,
      jsonSerialiser = jsonSerialiser)
    client.send(Channel(name), event) must beEqualTo("send OK").await
  }

  def sendAnyRefToEvent = {
    prop { (name: String, key: Key, secret: Secret, widget: String, doodad: Int, fuzz01: Boolean, fuzz02: Boolean) =>
      val (requestBuilder, httpRequestor, jsonSerialiser) = mocks
      val event = SampleData(widget, doodad, Seq(fuzz01, fuzz02))
      val eventJson = s"""{"widget":"$widget","doodad":$doodad,"fuzz":[$fuzz01,$fuzz02]}""" // not really, but lets pretend

      jsonSerialiser(event) returns eventJson
      requestBuilder(endWith("/event"), ===(Map("data" -> eventJson, "channel" -> name)), any[Credentials]) returns sendRequest
      httpRequestor(sendRequest) returns Future("send OK")

      val client = new EventSourceClient(key, secret, requestBuilder = requestBuilder, httpRequestor = httpRequestor,
        jsonSerialiser = jsonSerialiser)
      client.sendJson(Channel(name), event) must beEqualTo("send OK").await
    }
  }

  def temperatureExample = {
    val (requestBuilder, httpRequestor, _) = mocks
    val event = WeatherAlert(42.3, new Date(999999L))
    val eventString = """{"temperature":42.3,"time":"1970-01-01T00:16:39.999Z"}"""
    requestBuilder(endWith("/event"), ===(Map("data" -> eventString, "channel" -> "weather")), any[Credentials]) returns sendRequest
    httpRequestor(sendRequest) returns Future("send OK")
    val client = new EventSourceClient(Key(""), Secret(""), requestBuilder = requestBuilder, httpRequestor = httpRequestor,
      jsonSerialiser = EventSourceClient.jsonSerialiser)
    client.sendJson(Channel("weather"), event) must beEqualTo("send OK").await
  }

  def openError = prop { (name: String, key: Key, secret: Secret) =>
    onOpen(name, key, secret, new ConnectException("simulated"))
  }

  def openFailure = prop { (name: String, key: Key, secret: Secret) =>
    onOpen(name, key, secret, StatusCode(404))
  }

  def sendError = prop { (name: String, key: Key, secret: Secret, event: String) =>
    onSend(name, key, secret, event, new ConnectException("simulated"))
  }

  def sendFailure = prop { (name: String, key: Key, secret: Secret, event: String) =>
    onSend(name, key, secret, event, StatusCode(404))
  }

  private def onOpen(name: String, key: Key, secret: Secret, throwable: Throwable) = {
    val (requestBuilder, httpRequestor, jsonSerialiser) = mocks
    requestBuilder(endWith("/socket"), ===(Map("channel" -> name)), any[Credentials]) returns openRequest
    httpRequestor(openRequest) returns Future({throw throwable; ""})
    val client = new EventSourceClient(key, secret, requestBuilder = requestBuilder, httpRequestor = httpRequestor,
      jsonSerialiser = jsonSerialiser)
    val result = client.open(Channel(name))
    Await.ready(result, Duration(100, MILLISECONDS))
    result.failed must beEqualTo(throwable).await
  }

  private def onSend(name: String, key: Key, secret: Secret, event: String, throwable: Throwable) = {
    val (requestBuilder, httpRequestor, jsonSerialiser) = mocks
    requestBuilder(endWith("/event"), ===(Map("data" -> event, "channel" -> name)), any[Credentials]) returns sendRequest
    httpRequestor(sendRequest) returns Future({throw throwable; ""})
    val client = new EventSourceClient(key, secret, requestBuilder = requestBuilder, httpRequestor = httpRequestor,
      jsonSerialiser = jsonSerialiser)
    val result = client.send(Channel(name), event)
    Await.ready(result, Duration(100, MILLISECONDS))
    result.failed must beEqualTo(throwable).await
  }

  private def mocks = (mock[(String, Map[String, String], Credentials) => Req], mock[(Req) => Future[String]],
    mock[AnyRef => String])

}
case class SampleData(widget: String, doodad: Int, fuzz: Seq[Boolean])
case class WeatherAlert(temperature: Double, time: Date)
