package com.github.synesso.eshq

import org.specs2.Specification
import org.specs2.ScalaCheck
import java.util.Date

class AuthTokenSpec extends Specification with ScalaCheck with ArbitraryValues { def is = s2"""

  An auth token must
    be hexadecimal $hex
    have 40 digits $fortyDigits
    match exactly canned response $cannedResponse

"""

  def hex = prop{(key: Key, secret: Secret, time: Long) =>
    new AuthToken(key, secret, time).asHexString must beMatching("\\p{XDigit}+")
  }

  def fortyDigits = prop{(key: Key, secret: Secret, time: Long) =>
    new AuthToken(key, secret, time).asHexString must haveLength(40)
  }

  def cannedResponse = new AuthToken(Key("123"), Secret("abc"), new Date(1999).getTime / 1000).asHexString must
    beEqualTo("745e033158896561bce9ed983e7a3ddf03e84355")


}
