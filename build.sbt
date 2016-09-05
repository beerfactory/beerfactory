lazy val commonSettings = Seq(
  organization := "org.beerfactory",
  scalaVersion := "2.11.8",
  version := "0.1.0-SNAPSHOT"
)

logBuffered in Test := false

lazy val beerfactoryBackend = (project in file("backend"))
  .settings(commonSettings:_*)
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(SbtWeb)
  .settings(
    name := "backend",
    buildInfoPackage := "org.beerfactory.backend.version",
    buildInfoObject := "BuildInfo",
    buildInfoKeys := Seq[BuildInfoKey](
      name, version,buildInfoBuildNumber,
      "projectName" -> "Beerfactory"),
    buildInfoOptions += BuildInfoOption.BuildTime,
    libraryDependencies ++= Dependencies.backendDependencies
  )

lazy val beerfactoryFrontend = (project in file("frontend"))
  .settings(commonSettings)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    persistLauncher in Compile := true,
    persistLauncher in Test := false,
    libraryDependencies ++= Dependencies.frontendDependencies.value,
    jsDependencies ++= Dependencies.jsDependencies.value,
    scalaJSUseRhino in Global := false,
    LessKeys.compress in Assets := true
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