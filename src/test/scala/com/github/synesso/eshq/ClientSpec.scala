package com.github.synesso.eshq

import org.specs2.{ScalaCheck, Specification}
import scala.concurrent.{Await, ExecutionContext, Future}
import ExecutionContext.Implicits.global
import dispatch.{StatusCode, Req}
import org.specs2.mock.Mockito
import java.net.ConnectException
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit._

class ClientSpec extends Specification with Mockito with ScalaCheck with ArbitraryValues { def is = s2"""

  An EventSourceClient must
    send a request to /socket with provided channel name on open $sendToSocket
    send a request to /event with channel and data on send $sendToEvent
    report the open request error $openError
    report the open request failure $openFailure
    report the send request error $sendError
    report the send request failure $sendFailure

"""

  val openRequest = Req(identity)
  val sendRequest = Req(identity)

  def sendToSocket = prop { (name: String, key: Key, secret: Secret) =>
    val (requestBuilder, httpRequestor) = mocks

    requestBuilder(endWith("/socket"), ===(Map("channel" -> name)), any[Credentials]) returns openRequest
    httpRequestor(openRequest) returns Future("openOK")

    val client = new EventSourceClient(key, secret, requestBuilder = requestBuilder, httpRequestor = httpRequestor)
    client.open(Channel(name)) must beEqualTo("openOK").await
  }

  def sendToEvent = prop { (name: String, key: Key, secret: Secret, event: String) =>
    val (requestBuilder, httpRequestor) = mocks

    requestBuilder(endWith("/socket"), ===(Map("channel" -> name)), any[Credentials]) returns openRequest
    requestBuilder(endWith("/event"), ===(Map("data" -> event, "channel" -> name)), any[Credentials]) returns sendRequest

    httpRequestor(openRequest) returns Future("open OK")
    httpRequestor(sendRequest) returns Future("send OK")

    val client = new EventSourceClient(key, secret, requestBuilder = requestBuilder, httpRequestor = httpRequestor)
    client.send(Channel(name), event) must beEqualTo("send OK").await
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
    val client = new EventSourceClient(key, secret, requestBuilder = requestBuilder, httpRequestor = httpRequestor)
    val result = client.open(Channel(name))
    Await.ready(result, Duration(100, MILLISECONDS))
    result.failed must beEqualTo(throwable).await
  }

  private def onSend(name: String, key: Key, secret: Secret, event: String, throwable: Throwable) = {
    val (requestBuilder, httpRequestor) = mocks
    requestBuilder(endWith("/event"), ===(Map("data" -> event, "channel" -> name)), any[Credentials]) returns sendRequest
    httpRequestor(sendRequest) returns Future({throw throwable; ""})
    val client = new EventSourceClient(key, secret, requestBuilder = requestBuilder, httpRequestor = httpRequestor)
    val result = client.send(Channel(name), event)
    Await.ready(result, Duration(100, MILLISECONDS))
    result.failed must beEqualTo(throwable).await
  }

  private def mocks = (mock[(String, Map[String, String], Credentials) => Req], mock[(Req) => Future[String]])

}
