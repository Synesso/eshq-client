## The EventSource HQ Scala Client

_Not ready for use at this time!_

[![Build Status](https://api.travis-ci.org/Synesso/eshq-client.png)](https://travis-ci.org/Synesso/eshq-client)

### Pre-requisite

Make sure you have an [EventSource HQ](http://www.eventsourcehq.com/) account.

### Install

Add the following dependency to your `build.sbt`

_Not yet published to sonatype!_

```scala
libraryDependencies ++= Seq(
  "com.github.synesso" %% "eshq" % "0.1-SNAPSHOT"
)
```

### Example usage

```scala
import com.github.synesso.eshq._

// create a client with your ESHQ credentials
val client = new EventSourceClient(Key("my-key"), Secret("my-secret"))

// open a channel
val channel = client.open("channel-name")

// send an event
val res: Future[String] = channel.send("""{"msg": "Hello, World!"}"""})
```

The EventSourceClient is used to create multiple Channels with the same credentials.
If this is not needed, simply instantiate the Channel directly

```scala
val channel = Channel("a channel",  Key("a"), Secret("b"))
```

Both the EventSourceClient and Channel constructors take an optional `serviceURL` parameter,
should it differ from the default value of `http://app.eventsourcehq.com`

```scala
val client = new EventSourceClient(Key("a"), Secret("b"), new URL("http://non-default-ho.st"))
// or
val channel = Channel("a channel",  Key("a"), Secret("b"), new URL("http://non-default-ho.st"))
```