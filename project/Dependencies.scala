import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._


object Dependencies {
  // Versions
  object V {
    val akka = "2.4.10"
    val scalactic = "3.0.0"
    val scalatest = "3.0.0"
    val typeSafeLogging = "3.4.0"
    val logbackClassic = "1.1.7"
    val scalajsReact = "0.11.1"
    val scalaCSS = "0.5.0"
    val diode = "1.0.0"
    val scalaDom = "0.9.1"
    val playJson = "2.5.6"
    val slick = "3.1.1"
    val slickPg = "0.14.3"
    val liquibase = "3.5.1"
    val hsqldb = "2.3.4"
    val akkaHttpJson = "1.9.0"
    val jwtScala = "0.8.0"
    val scalatags = "0.6.0"
  }

  object jsV {
    val react = "15.3.1"
    val semanticUI = "2.2.2"
    val jQuery = "3.1.0"
  }

  val typesafeConfig  = "com.typesafe" %% "config" % "1.3.0"
  val typesafeLogging = "com.typesafe.scala-logging" % "scala-logging_2.11" % V.typeSafeLogging
  val logbackClassic = "ch.qos.logback" % "logback-classic" % V.logbackClassic

  val akkaActor = "com.typesafe.akka" %% "akka-actor" % V.akka
  val akkaStream = "com.typesafe.akka" % "akka-stream_2.11" % V.akka
  val akkaHttp = "com.typesafe.akka" %% "akka-http-experimental" % V.akka
  val akkaTestKit = "com.typesafe.akka" % "akka-testkit_2.11" % V.akka % Test
  val akkaStreamTestKit = "com.typesafe.akka" % "akka-stream-testkit_2.11" % V.akka
  val akkaHttpJson = "de.heikoseeberger" %% "akka-http-play-json" % V.akkaHttpJson
  val playJson = "com.typesafe.play" %% "play-json" % V.playJson
  val jwtScala = "com.pauldijou" %% "jwt-play-json" % V.jwtScala

  val scalactic = "org.scalactic" %% "scalactic" % V.scalactic

  // Test
  val scalatest = "org.scalatest" %% "scalatest" % V.scalatest % "test"

  val commonDependencies = Seq(typesafeLogging, logbackClassic, scalactic, scalatest)
  val dbDependencies = Seq(
    "com.typesafe.slick" %% "slick" % V.slick,
    "org.hsqldb" % "hsqldb" % V.hsqldb,
    "org.liquibase" % "liquibase-core" % V.liquibase,
    "org.postgresql" % "postgresql" % "9.4.1209",
    "com.mattbertolini" % "liquibase-slf4j" % "2.0.0",
    "com.github.tminglei" %% "slick-pg" % V.slickPg,
    "com.github.tminglei" %% "slick-pg_date2" % V.slickPg,
    "com.github.tminglei" %% "slick-pg_play-json" % V.slickPg,
    "com.typesafe.slick" %% "slick-hikaricp" % V.slick
  )

  val webDependencies = Seq(
    "com.lihaoyi" %% "scalatags" % V.scalatags,
    "com.github.japgolly.scalacss" %% "core" % V.scalaCSS,
    "org.webjars" % "webjars-locator" % "0.32",
    "org.webjars" % "font-awesome" % "4.6.3",
    "org.webjars" % "Semantic-UI" % jsV.semanticUI,
    "org.webjars" % "jquery" % jsV.jQuery
  )

  val backendDependencies = commonDependencies ++ dbDependencies ++ webDependencies ++ Seq(jwtScala, playJson, akkaActor, akkaStream, akkaHttp, akkaHttpJson, akkaTestKit)

  val frontendDependencies = Def.setting(Seq(
    "com.github.japgolly.scalajs-react" %%% "core" % V.scalajsReact,
    "com.github.japgolly.scalajs-react" %%% "extra" % V.scalajsReact,
    "com.github.japgolly.scalacss" %%% "ext-react" % V.scalaCSS,
    "me.chrons" %%% "diode" % V.diode,
    "me.chrons" %%% "diode-react" % V.diode,
    "org.scalatest" %%% "scalatest" % V.scalatest % "test"
  ))

  val jsDependencies = Seq(
    "org.webjars.bower" % "react" % jsV.react / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React",
    "org.webjars.bower" % "react" % jsV.react / "react-dom.js" minified "react-dom.min.js" dependsOn "react-with-addons.js" commonJSName "ReactDOM",
    "org.webjars.bower" % "react" % jsV.react / "react-dom-server.js" minified "react-dom-server.min.js" dependsOn "react-dom.js" commonJSName "ReactDOMServer",
    "org.webjars" % "jquery" % jsV.jQuery / "jquery.js" minified "jquery.min.js",
    "org.webjars" % "Semantic-UI" % jsV.semanticUI / "semantic.js" minified "semantic.min.js" dependsOn "jquery.js"
  )
}