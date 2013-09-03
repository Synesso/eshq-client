package com.github.synesso.eshq


class Token(key: Key, secret: Secret, time: Long) {

  lazy val asHexString = {
    val md = java.security.MessageDigest.getInstance("SHA-1")
    md.digest(s"${key.value}:${secret.value}:$time".getBytes("UTF-8")).map("%02x".format(_)).mkString
  }

}
