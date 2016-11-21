import sbt.{Resolver, _}
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import ScalaJSPlugin._
import autoImport._

val scOptions = Seq(
  "-Xlint",
  "-unchecked",
  "-deprecation",
  "-feature",
  "-language:postfixOps"
)

lazy val commonSettings = Seq(
  organization := "org.beerfactory",
  scalaVersion := "2.11.8",
  version := "0.1.0-SNAPSHOT",
  resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/",
  resolvers += Resolver.jcenterRepo
)

logBuffered in Test := false

lazy val root = (project in file(".")).aggregate(server, clientApp, clientRegistration)

lazy val server = (project in file("server"))
  .settings(commonSettings: _*)
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(SbtWeb)
  .enablePlugins(PlayScala)
  .dependsOn(sharedJVM)
  .settings(
    name := "server",
    scalacOptions ++= scOptions,
    scalaJSProjects := Seq(clientApp, clientRegistration),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest), //, gzip),
    compile in Compile <<= (compile in Compile) dependsOn scalaJSPipeline,
    LessKeys.compress in Assets := true,
    buildInfoPackage := "org.beerfactory.server.version",
    buildInfoObject := "BuildInfo",
    buildInfoKeys := Seq[BuildInfoKey](name, version, "projectName" -> "Beerfactory"),
    buildInfoOptions += BuildInfoOption.BuildTime,
    libraryDependencies ++= Dependencies.commonDependencies.value ++ Dependencies.serverDependencies.value,
    libraryDependencies += filters
  )

lazy val clientApp = (project in file("client-app"))
  .settings(commonSettings)
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(sharedJS)
  .settings(
    persistLauncher in Compile := true,
    //persistLauncher in Test := false,
    //scalaJSUseRhino in Global := false,
    libraryDependencies ++= Dependencies.commonDependencies.value ++ Dependencies.clientDependencies.value,
    jsDependencies ++= Dependencies.jsDependencies ++ Seq(RuntimeDOM),
    //jsEnv := JSDOMNodeJSEnv().value,
    skip in packageJSDependencies := false
  )

lazy val clientRegistration = (project in file("client-registration"))
  .settings(commonSettings)
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(sharedJS)
  .settings(
    persistLauncher in Compile := true,
    //persistLauncher in Test := false,
    //scalaJSUseRhino in Global := false,
    libraryDependencies ++= Dependencies.commonDependencies.value ++ Dependencies.clientDependencies.value,
    jsDependencies ++= Dependencies.jsDependencies ++ Seq(RuntimeDOM),
    //jsEnv := JSDOMNodeJSEnv().value,
    skip in packageJSDependencies := false
  )

lazy val shared = (crossProject in file("shared"))
  .settings(commonSettings: _*)
  .jsConfigure(_ enablePlugins ScalaJSWeb)
  .settings(
    libraryDependencies ++= Dependencies.commonDependencies.value
  )
  .jsSettings(
    libraryDependencies ++= Dependencies.sharedDependencies.value
  )

lazy val sharedJVM = shared.jvm.settings(name := "sharedJVM")
lazy val sharedJS  = shared.js.settings(name := "sharedJS")

// loads the server project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value
