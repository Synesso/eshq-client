## The EventSource HQ Scala Client

_Not ready for use at this time_

[![Build Status](https://api.travis-ci.org/Synesso/scala-eshq.png)](https://travis-ci.org/Synesso/scala-eshq)

## Pre-requisite

Make sure you have an [EventSource HQ](http://www.eventsourcehq.com/) account.

## Install

Add the following dependency to your `build.sbt`

```scala
libraryDependencies ++= Seq(
  "com.github.synesso" %% "scala-eshq" % "0.1"
)
```

## Example usage

```scala
import com.github.synesso.eshq._

// create a client with your ESHQ credentials
val client = new EventSourceClient(Key("my-key"), Secret("my-secret"))

// open a channel
val channel = client.open("channel-name")

// send an event
val res: Future[String] = channel.send("""{"msg": "Hello, World!"}"""})
```

The `EventSourceClient` is used to create multiple `Channel`s with the same credentials.
If this is not needed, simply create the `Channel` directly

```scala
val channel = new Channel("a channel",  Key("a"), Secret("b"))
```

Both the `EventSourceClient` and `Channel` constructors take an optional `serviceURL` parameter,
should it differ from the default value of `http://app.eventsourcehq.com`

```scala
val client = new EventSourceClient(Key("a"), Secret("b"), new URL("http://non-default-ho.st"))
// or
val channel = new Channel("a channel",  Key("a"), Secret("b"), new URL("http://non-default-ho.st"))
```