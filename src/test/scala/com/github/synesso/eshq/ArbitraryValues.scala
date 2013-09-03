package com.github.synesso.eshq

import org.scalacheck.Arbitrary

trait ArbitraryValues {

  implicit val key = Arbitrary(for (string <- Arbitrary.arbString.arbitrary) yield Key(string))
  implicit val secret = Arbitrary(for (string <- Arbitrary.arbString.arbitrary) yield Secret(string))

}
