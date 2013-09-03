# ESHQ Client for Scala

_Not ready for use at this time_

[![Build Status](https://api.travis-ci.org/Synesso/scala-eshq.png)](https://travis-ci.org/Synesso/scala-eshq)


    val client = new EventSourceClient(Key("a"), Secret("b"))
    val channel = client.open("a channel")
    val result = channel.send("some data")


    val channel = new Channel("a channel",  Key("a"), Secret("b"))
    val result = channel.send("some data")


    val client = new EventSourceClient(Key("a"), Secret("b"), new URL("http://non-default-ho.st"))

    val channel = new Channel("a channel",  Key("a"), Secret("b"), new URL("http://non-default-ho.st"))
