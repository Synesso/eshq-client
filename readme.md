## The EventSource HQ Scala Client

[![Build Status](https://api.travis-ci.org/Synesso/eshq-client.png)](https://travis-ci.org/Synesso/eshq-client)

### Pre-requisite

Make sure you have an [EventSource HQ](http://www.eventsourcehq.com/) account.

### Install

Add the following dependency to your `build.sbt`:

```scala
libraryDependencies ++= Seq(
  "com.github.synesso" %% "eshq" % "0.1-SNAPSHOT"
)
```

Make sure you have the sonatype OSS snapshop resolver configured:

```scala
resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)
```

### Example usage

```scala
import com.github.synesso.eshq._

// create a client with your ESHQ credentials
val client = new EventSourceClient(Key("my-key"), Secret("my-secret"))

// open a channel and receive the channel data from ESHQ
val channelData: Future[String] = client.open(Channel("my-channel"))

// send an event to a channel as a String
client.send(Channel("my-channel"), """{"msg": "Hello, World!"}"""})

// send any reference as an event to a channel as JSON
case class WeatherAlert(temperature: Double, time: Date)
client.send(Channel("my-channel"), WeatherAlert(42.2, new Date))
```

The EventSourceClient takes an optional `serviceURL` parameter, should it differ from the
default value of `http://app.eventsourcehq.com`

```scala
val client = new EventSourceClient(Key("a"), Secret("b"), new URL("http://non.default-ho.st"))
```