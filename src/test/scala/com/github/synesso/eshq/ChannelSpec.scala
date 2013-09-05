package com.github.synesso.eshq

import org.specs2.{ScalaCheck, Specification}
import org.specs2.mock.Mockito
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import dispatch.Req

class ChannelSpec extends Specification with Mockito with ScalaCheck with ArbitraryValues { def is = s2"""

  A Channel must
    send a request to /socket with provided channel name on instantiation $sendToSocket
    send a request to /event with channel and data $sendToEvent

"""

  def sendToSocket = prop { (name: String, key: Key, secret: Secret) =>
    val requestBuilder = mock[(String, Map[String, String], Credentials) => Req]
    val httpRequestor = mock[(Req) => Future[String]]
    val request = Req(identity)

    requestBuilder(endWith("/socket"), ===(Map("channel" -> name)), any[Credentials]) returns request
    httpRequestor(request) returns Future("")

    new Channel(name, key, secret, requestBuilder = requestBuilder, httpRequestor = httpRequestor)
    there was one(httpRequestor).apply(request)
  }

  def sendToEvent = prop { (name: String, key: Key, secret: Secret, event: String) =>
    val requestBuilder = mock[(String, Map[String, String], Credentials) => Req]
    val httpRequestor = mock[(Req) => Future[String]]
    val openRequest = Req(identity)
    val sendRequest = Req(identity)

    requestBuilder(endWith("/socket"), ===(Map("channel" -> name)), any[Credentials]) returns openRequest
    requestBuilder(endWith("/event"), ===(Map("data" -> event, "channel" -> name)), any[Credentials]) returns sendRequest

    httpRequestor(openRequest) returns Future("open OK")
    httpRequestor(sendRequest) returns Future("send OK")

    val channel = new Channel(name, key, secret, requestBuilder = requestBuilder, httpRequestor = httpRequestor)

    val actualResult = channel.send(event)
    actualResult must beEqualTo("send OK").await
  }

}