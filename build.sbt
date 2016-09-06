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
    .aggregate(beerfactoryBackend, beerfactoryFrontend)

lazy val beerfactoryBackend = (project in file("backend"))
  .settings(commonSettings:_*)
  .enablePlugins(BuildInfoPlugin)
  .dependsOn(beerfactorySharedJVM)
  .settings(
    name := "backend",
    buildInfoPackage := "org.beerfactory.backend.version",
    buildInfoObject := "BuildInfo",
    buildInfoKeys := Seq[BuildInfoKey](
      name, version,buildInfoBuildNumber,
      "projectName" -> "Beerfactory"),
    buildInfoOptions += BuildInfoOption.BuildTime,
    libraryDependencies ++= Dependencies.backendDependencies,
    //(resources in Compile) += (fastOptJS in (beerfactoryFrontend, Compile)).value.data,
    (resourceGenerators in Compile) <+=
      (fastOptJS in Compile in beerfactoryFrontend, packageScalaJSLauncher in Compile in beerfactoryFrontend)
        .map((f1, f2) => Seq(f1.data, f2.data)),
    watchSources <++= (watchSources in beerfactoryFrontend)
  )

lazy val beerfactoryFrontend = (project in file("frontend"))
  .settings(commonSettings)
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(beerfactorySharedJS)
  .settings(
    persistLauncher in Compile := true,
    persistLauncher in Test := false,
    libraryDependencies ++= Dependencies.frontendDependencies.value,
    jsDependencies ++= Dependencies.jsDependencies.value,
    scalaJSUseRhino in Global := false
  )

lazy val beerfactoryShared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(commonSettings:_*).
  jvmSettings(
    // Add JVM-specific settings here
  ).
  jsSettings(
    // Add JS-specific settings here
  )

lazy val beerfactorySharedJVM = beerfactoryShared.jvm.settings(name := "beerfactorySharedJVM")
lazy val beerfactorySharedJS = beerfactoryShared.js.settings(name := "beerfactorySharedJS")