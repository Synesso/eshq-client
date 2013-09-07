package com.github.synesso.eshq

import java.util.Date

class Credentials private[eshq] (key: Key, secret: Secret, time: Date) {

  def asMap: Map[String, String] = {
    val timestamp = time.getTime / 1000
    Map(
      "key" -> key.value,
      "timestamp" -> s"$timestamp",
      "token" -> new AuthToken(key, secret, timestamp).asHexString
    )
  }
}

object Credentials {
  def apply(key: Key, secret: Secret) = new Credentials(key, secret, new Date)
}
