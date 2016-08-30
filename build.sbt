lazy val commonSettings = Seq(
  organization := "org.beerfactory",
  scalaVersion := "2.11.8",
  version := "0.1.0"
)

lazy val beerfactoryBackend = (project in file("backend"))
  .settings(commonSettings:_*)
  .settings(
    name := "backend",
    libraryDependencies ++= Dependencies.serverDependencies
  )

logBuffered in Test := false
//parallelExecution in Test := false