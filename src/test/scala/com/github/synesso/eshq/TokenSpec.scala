package com.github.synesso.eshq

import org.specs2.Specification
import org.specs2.ScalaCheck
import org.scalacheck.Arbitrary
import java.util.Date

class TokenSpec extends Specification with ScalaCheck { def is = s2"""

  A token must
    be hexidecimal $hex
    have 40 digits $fortyDigits

"""

  def hex = prop{(key: Key, secret: Secret, time: Date) =>
    new Token(key, secret, time).asHexString must beMatching("\\p{XDigit}+")
  }

  def fortyDigits =  prop{(key: Key, secret: Secret, time: Date) =>
    new Token(key, secret, time).asHexString must haveLength(40)
  }

  implicit val key = Arbitrary(for (string <- Arbitrary.arbString.arbitrary) yield new Key(string))
  implicit val secret = Arbitrary(for (string <- Arbitrary.arbString.arbitrary) yield new Secret(string))



}
