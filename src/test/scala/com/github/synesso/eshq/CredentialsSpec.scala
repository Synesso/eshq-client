package com.github.synesso.eshq

import org.specs2.{Specification, ScalaCheck}
import org.specs2.mock.Mockito
import java.util.Date

class CredentialsSpec extends Specification with Mockito with ScalaCheck with ArbitraryValues { def is = s2"""

  Credentials must
    convert to a map with mandatory parameters $convertToMap

"""

  def convertToMap = prop{(key: Key, secret: Secret, time: Date) =>
    val timestamp = time.getTime / 1000
    val token = new AuthToken(key, secret, timestamp).asHexString
    val map = new Credentials(key, secret, time).asMap
    map must havePairs("key" -> key.value, "timestamp" -> s"$timestamp", "token" -> token)
  }

}
