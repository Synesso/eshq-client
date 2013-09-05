package com.github.synesso.eshq

import org.specs2.{ScalaCheck, Specification}
import org.specs2.mock.Mockito
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import dispatch.Req

class ChannelSpec extends Specification with Mockito with ScalaCheck with ArbitraryValues { def is = s2"""

  A Channel must
    send an open request to /socket with provided channel name on instantiation $sendToSocket

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

}