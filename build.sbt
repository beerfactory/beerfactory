import sbt._
import Keys._

import org.scalajs.sbtplugin.ScalaJSPlugin
import ScalaJSPlugin._
import autoImport._

lazy val commonSettings = Seq(
  organization := "org.beerfactory",
  scalaVersion := "2.11.8",
  version := "0.1.0-SNAPSHOT"
)

logBuffered in Test := false

lazy val root = (project in file("."))
    .aggregate(backend, frontend)

lazy val backend = (project in file("backend"))
  .settings(Revolver.settings: _*)
  .settings(commonSettings:_*)
  .enablePlugins(BuildInfoPlugin)
  .dependsOn(sharedJVM)
  .settings(
    name := "backend",
    buildInfoPackage := "org.beerfactory.backend.version",
    buildInfoObject := "BuildInfo",
    buildInfoKeys := Seq[BuildInfoKey](
      name, version,buildInfoBuildNumber,
      "projectName" -> "Beerfactory"),
    buildInfoOptions += BuildInfoOption.BuildTime,
    libraryDependencies ++= Dependencies.backendDependencies,
    (resources in Compile) += (fastOptJS in (frontend, Compile)).value.data,
    watchSources <++= (watchSources in frontend)
  )

lazy val frontend = (project in file("frontend"))
  .settings(commonSettings)
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(sharedJS)
  .settings(
    //persistLauncher in Compile := true,
    //persistLauncher in Test := false,
    libraryDependencies ++= Dependencies.frontendDependencies.value
    ,jsDependencies ++= Dependencies.jsDependencies.value
    //,scalaJSUseRhino in Global := false
  )

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(commonSettings:_*).
  jvmSettings(
    // Add JVM-specific settings here
  ).
  jsSettings(
    // Add JS-specific settings here
  )

lazy val sharedJVM = shared.jvm.settings(name := "sharedJVM")
lazy val sharedJS = shared.js.settings(name := "sharedJS")