package com.github.synesso.eshq

import org.specs2.{ScalaCheck, Specification}
import scala.concurrent.{Await, ExecutionContext, Future, Awaitable}
import ExecutionContext.Implicits.global
import dispatch.{StatusCode, Req}
import org.specs2.specification.After
import org.mockito.{Mockito => Mocks}
import org.specs2.mock.Mockito
import org.scalacheck.Prop
import java.net.ConnectException
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit._

class ChannelSpec extends Specification with Mockito with ScalaCheck with ArbitraryValues { def is = s2"""

  A Channel must
    send a request to /socket with provided channel name on instantiation $sendToSocket
    send a request to /event with channel and data $sendToEvent
    report the open request error on first send attempt $openError
    report the open request failure on first send attempt $openFailure
    report the send request error $sendError
    report the send request failure $sendFailure

"""

  val openRequest = Req(identity)
  val sendRequest = Req(identity)

  def sendToSocket = prop { (name: String, key: Key, secret: Secret) =>
    val (requestBuilder, httpRequestor) = mocks

    requestBuilder(endWith("/socket"), ===(Map("channel" -> name)), any[Credentials]) returns openRequest
    httpRequestor(openRequest) returns Future("openOK")

    new Channel(name, key, secret, requestBuilder = requestBuilder, httpRequestor = httpRequestor)
    there was one(httpRequestor).apply(openRequest)
  }

  def sendToEvent = prop { (name: String, key: Key, secret: Secret, event: String) =>
    val (requestBuilder, httpRequestor) = mocks

    requestBuilder(endWith("/socket"), ===(Map("channel" -> name)), any[Credentials]) returns openRequest
    requestBuilder(endWith("/event"), ===(Map("data" -> event, "channel" -> name)), any[Credentials]) returns sendRequest

    httpRequestor(openRequest) returns Future("open OK")
    httpRequestor(sendRequest) returns Future("send OK")

    val channel = new Channel(name, key, secret, requestBuilder = requestBuilder, httpRequestor = httpRequestor)

    channel.send(event) must beEqualTo("send OK").await
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
    val (requestBuilder, httpRequestor) = mocks
    requestBuilder(endWith("/socket"), ===(Map("channel" -> name)), any[Credentials]) returns openRequest
    httpRequestor(openRequest) returns Future({throw throwable; ""})
    val channel = new Channel(name, key, secret, requestBuilder = requestBuilder, httpRequestor = httpRequestor)
    val result = channel.send("anything")
    Await.ready(result, Duration(100, MILLISECONDS))
    result.failed must beEqualTo(throwable).await
  }

  private def onSend(name: String, key: Key, secret: Secret, event: String, throwable: Throwable) = {
    val (requestBuilder, httpRequestor) = mocks
    requestBuilder(endWith("/socket"), ===(Map("channel" -> name)), any[Credentials]) returns openRequest
    requestBuilder(endWith("/event"), ===(Map("data" -> event, "channel" -> name)), any[Credentials]) returns sendRequest
    httpRequestor(openRequest) returns Future("open OK")
    httpRequestor(sendRequest) returns Future({throw throwable; ""})
    val channel = new Channel(name, key, secret, requestBuilder = requestBuilder, httpRequestor = httpRequestor)
    val result = channel.send(event)
    Await.ready(result, Duration(100, MILLISECONDS))
    result.failed must beEqualTo(throwable).await
  }

  private def mocks = (mock[(String, Map[String, String], Credentials) => Req], mock[(Req) => Future[String]])

}
