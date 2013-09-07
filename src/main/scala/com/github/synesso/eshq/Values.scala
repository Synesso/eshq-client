package com.github.synesso.eshq

// value types to ensure String parameters are not passed in the wrong order

case class Key(value: String) extends AnyVal

case class Secret(value: String) extends AnyVal

case class Channel(name: String) extends AnyVal
