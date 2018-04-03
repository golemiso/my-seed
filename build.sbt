name := """splatoon-tournament-manager"""
organization := "com.golemiso"

version := "0.1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala, DockerPlugin)

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  guice,
  "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
  "org.reactivemongo" %% "reactivemongo" % "0.13.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.golemiso.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.golemiso.binders._"

javaOptions in Test += "-Dconfig.file=test/resources/test.conf"
