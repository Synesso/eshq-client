package com.github.synesso.eshq

import java.net.URL

class EventSourceClient(val key: Key, val secret: Secret, val serviceURL: URL = EventSourceClient.defaultURL) {

  def open(channelName: String): Channel = {
    new Channel(channelName, key, secret, serviceURL)
  }
}

object EventSourceClient {
  val defaultURL = new URL("http://app.eventsourcehq.com")
}
