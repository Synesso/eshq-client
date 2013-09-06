organization := "com.github.synesso"

name := "eshq"

scalaVersion := "2.10.2"

version := "0.1"

libraryDependencies ++= Seq(
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
  "org.slf4j" % "slf4j-simple" % "1.7.5",
  "org.specs2" %% "specs2" % "2.2" % "test",
  "org.hamcrest"  %  "hamcrest-all" % "1.3" % "test",
  "org.scalacheck" %% "scalacheck" % "1.10.0" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test"
)

resolvers ++= Seq(
  "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)
