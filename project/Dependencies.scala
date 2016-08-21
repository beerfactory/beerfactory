import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._


object Dependencies {
  // Versions
  object V {
    val akka = "2.4.9"
    val scalactic = "3.0.0"
    val scalatest = "3.0.0"
    val typeSafeLogging = "3.4.0"
    val logbackClassic = "1.1.7"
    val scalajsReact = "0.11.1"
    val scalaCSS = "0.4.1"
    val diode = "1.0.0"
    val scalaDom = "0.9.1"
    val playJson = "2.5.0"
    val slick = "3.1.1"
    val h2 = "1.4.192"
  }

  object jsV {
    val react = "15.3.0"
    val semanticUI = "2.2.2"
    val jQuery = "3.1.0"
  }

  val typesafeConfig  = "com.typesafe" %% "config" % "1.3.0"
  val typesafeLogging = "com.typesafe.scala-logging" % "scala-logging_2.11" % V.typeSafeLogging
  val logbackClassic = "ch.qos.logback" % "logback-classic" % V.logbackClassic

  val akkaActor = "com.typesafe.akka" %% "akka-actor" % V.akka
  val akkaStream = "com.typesafe.akka" % "akka-stream_2.11" % V.akka
  val akkaTestKit = "com.typesafe.akka" % "akka-testkit_2.11" % V.akka
  val akkaStreamTestKit = "com.typesafe.akka" % "akka-stream-testkit_2.11" % V.akka
  val playJson = "com.typesafe.play" %% "play-json" % V.playJson

  val scalactic = "org.scalactic" %% "scalactic" % V.scalactic

  val flyway = "org.flywaydb" % "flyway-core" % "4.0.3"
  // Test
  val scalatest = "org.scalatest" %% "scalatest" % V.scalatest % "test"

  val commonDependencies = Seq(typesafeLogging, logbackClassic, scalatest)
  val dbDependencies = Seq(
    "com.typesafe.slick" %% "slick" % V.slick,
    "com.h2database" % "h2" % V.h2
  )
  val serverDependencies = commonDependencies ++ dbDependencies ++ Seq(akkaActor, akkaStream)

  val scalajsDependencies = Def.setting(Seq(
    "com.github.japgolly.scalajs-react" %%% "core" % V.scalajsReact,
    "com.github.japgolly.scalajs-react" %%% "extra" % V.scalajsReact,
    "com.github.japgolly.scalacss" %%% "ext-react" % V.scalaCSS,
    "me.chrons" %%% "diode" % V.diode,
    "me.chrons" %%% "diode-react" % V.diode,
    "org.scala-js" %%% "scalajs-dom" % V.scalaDom
  ))

  /** Dependencies only used by the JVM project */
  val frontendServerDependencies = Def.setting(Seq(
    "org.webjars" %% "webjars-play" % "2.5.0",
    "org.webjars" % "font-awesome" % "4.6.3",
    "org.webjars" % "Semantic-UI" % jsV.semanticUI % Provided
  ))

  val jsDependencies = Def.setting(Seq(
    "org.webjars.bower" % "react" % jsV.react / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React",
    "org.webjars.bower" % "react" % jsV.react / "react-dom.js" minified "react-dom.min.js" dependsOn "react-with-addons.js" commonJSName "ReactDOM",
    "org.webjars" % "Semantic-UI" % jsV.semanticUI / "semantic.js" minified "semantic.min.js",
    "org.webjars" % "jquery" % jsV.jQuery / "jquery.js" minified "jquery.min.js"
  ))
}