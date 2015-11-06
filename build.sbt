name := "ide"

version := "1.0"

scalaVersion := "2.11.7"

mainClass in assembly := Some("codingchallenge.Runner")

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % "test",
  "org.json4s" %% "json4s-core" % "3.3.0",
  "org.json4s" %% "json4s-jackson" % "3.3.0",
  "joda-time" % "joda-time" % "2.9",
  "org.joda" % "joda-convert" % "1.8"
)

