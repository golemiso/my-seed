val baseSettings = Seq(
  organization := "com.golemiso",
  version := "0.1.0",
  scalaVersion := "2.12.4",
  javaOptions in Test += "-Dconfig.file=test/resources/test.conf"
)

lazy val `domain` = (project in file("domain"))
  .settings(baseSettings)
  .settings(
    name := "ama-domain"
  )

lazy val `play` = (project in file("play")).enablePlugins(PlayScala)
  .settings(baseSettings)
  .settings(
    name := "ama-play",
    libraryDependencies ++= Seq(
      guice,
      "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
      "org.reactivemongo" %% "reactivemongo" % "0.13.0",
      "org.reactivemongo" %% "reactivemongo-akkastream" % "0.13.0",
      "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
    )
  )
  .dependsOn(`domain`)

lazy val root = (project in file(".")).enablePlugins(DockerPlugin)
  .settings(baseSettings)
  .settings(
    name := "ama"
  )
  .aggregate(`play`)
