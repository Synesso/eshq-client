package com.github.synesso.eshq

import java.util.Date

private[eshq] class Token(key: Key, secret: Secret, time: Date) {

  lazy val asHexString = {
    val timeAsLong = time.getTime / 1000
    val md = java.security.MessageDigest.getInstance("SHA-1")
    md.digest(s"${key.value}:${secret.value}:$timeAsLong".getBytes("UTF-8")).map("%02x".format(_)).mkString
  }

}
