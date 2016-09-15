import sbt._
import Keys._

import org.scalajs.sbtplugin.ScalaJSPlugin
import ScalaJSPlugin._
import autoImport._

scalacOptions += "-feature"

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
    resourceGenerators in Compile <+= Def.task {
      val f1 = (fastOptJS in Compile in frontend).value.data
      val f1SourceMap = f1.getParentFile / (f1.getName + ".map")
      val f2 = (packageScalaJSLauncher in Compile in frontend).value.data
      val f3 = (packageJSDependencies in Compile in frontend).value
      val f4 = (packageMinifiedJSDependencies in Compile in frontend).value
      Seq(f1, f1SourceMap, f2, f3, f4)
    },
    watchSources <++= (watchSources in frontend)
  )

lazy val frontend = (project in file("frontend"))
  .settings(commonSettings)
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(sharedJS)
  .settings(
    persistLauncher in Compile := true,
    persistLauncher in Test := false,
    scalaJSUseRhino in Global := false,
    libraryDependencies ++= Dependencies.frontendDependencies.value,
    jsDependencies ++= Dependencies.jsDependencies,
    jsEnv := JSDOMNodeJSEnv().value
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