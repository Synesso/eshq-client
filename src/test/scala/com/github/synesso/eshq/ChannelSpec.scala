package com.github.synesso.eshq

import org.specs2.{ScalaCheck, Specification}
import org.specs2.mock.Mockito
import scala.concurrent.Future
import dispatch.Req

class ChannelSpec extends Specification with Mockito with ScalaCheck with ArbitraryValues { def is = s2"""

  A Channel must
    send an open request to /socket with provided channel name on instantiation $sendToSocket


"""

  def sendToSocket = prop{(name: String, key: Key, secret: Secret) =>
    val requestBuilder = mock[(String, Map[String, String], Credentials) => Req]
    val request = mock[Req]
    val httpRequestor = mock[(Req) => Future[String]]
    val result = mock[Future[String]]

    val credentialsArg = argThat[Credentials, Credentials](haveClass[Credentials])

    requestBuilder("/socketz", Map("channel" -> name), credentialsArg) returns request
    httpRequestor(request) returns result
    new Channel(name, key, secret, requestBuilder = requestBuilder, httpRequestor = httpRequestor)
    there was one(httpRequestor).apply(request)
  }

}