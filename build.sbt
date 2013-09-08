organization := "com.github.synesso"

name := "eshq"

scalaVersion := "2.10.2"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
  "org.slf4j" % "slf4j-simple" % "1.7.5",
  "org.json4s" %% "json4s-jackson" % "3.2.5",
  "org.specs2" %% "specs2" % "2.2" % "test",
  "org.hamcrest"  %  "hamcrest-all" % "1.3" % "test",
  "org.scalacheck" %% "scalacheck" % "1.10.0" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test"
)

resolvers ++= Seq(
  "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)

publishMavenStyle := true

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>http://github.com/Synesso/eshq-client</url>
  <licenses>
    <license>
      <name>MIT</name>
      <url>http://opensource.org/licenses/MIT</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:Synesso/eshq-client.git</url>
    <connection>scm:git:git@github.com:Synesso/eshq-client.git</connection>
  </scm>
  <developers>
    <developer>
      <id>jmawson</id>
      <name>Jem Mawson</name>
    </developer>
  </developers>)