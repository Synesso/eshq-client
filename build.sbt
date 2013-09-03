organization := "com.github.synesso"

name := "scala-eshq"

scalaVersion := "2.10.2"

version := "0.1"

libraryDependencies ++= Seq(
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
  "org.specs2" %% "specs2" % "2.2-SNAPSHOT" % "test",
  "org.scalacheck" %% "scalacheck" % "1.10.0" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test"
)

resolvers ++= Seq(
  "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)
