package com.github.synesso.eshq

import org.specs2.{ScalaCheck, Specification}
import org.specs2.mock.Mockito

class OpenSocketSpec extends Specification with Mockito with ScalaCheck with ArbitraryValues { def is = s2"""

  An EventSourceClient must
    send an open request to /socket with provided channel $sendToSocket
    send an event to /event with provided channel $sendToEvent

"""

  def sendToSocket = pending
  def sendToEvent = pending

}