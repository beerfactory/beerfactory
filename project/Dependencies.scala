import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._


object Dependencies {
  // Versions
  object V {
    val akka = "2.4.10"
    val scalactic = "3.0.0"
    val scalatest = "3.0.0"
    val scalatestPlay = "1.5.0"
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
    val h2 = "1.4.192"
    val akkaHttpJson = "1.9.0"
    val jwtScala = "0.8.0"
    val scalatags = "0.6.0"
    val playScripts = "1.0.0"
    val silhouette = "4.0.0"
    val scalaGuice = "4.1.0"
    val webJar = "2.5.0"
    val flyway = "3.0.1"
  }

  object jsV {
    val react = "15.3.1"
    val semanticUI = "2.2.2"
    val jQuery = "3.1.0"
  }

  val sharedDependencies = Def.setting(Seq(
    "com.typesafe.scala-logging" % "scala-logging_2.11" % V.typeSafeLogging,
    "ch.qos.logback" % "logback-classic" % V.logbackClassic,
    "org.scalactic" %% "scalactic" % V.scalactic,
    "org.scalatest" %% "scalatest" % V.scalatest % "test"
  ))

  val serverDependencies = Def.setting(Seq(
    "com.typesafe.slick" %% "slick" % V.slick,
    "com.typesafe.play" %% "play-slick" % "2.0.0",
    "org.flywaydb" %% "flyway-play" % V.flyway,
    "com.h2database" % "h2" % V.h2,
    "org.liquibase" % "liquibase-core" % V.liquibase,
    "org.postgresql" % "postgresql" % "9.4.1209",
    "com.mattbertolini" % "liquibase-slf4j" % "2.0.0",
    "com.github.tminglei" %% "slick-pg" % V.slickPg,
    "com.github.tminglei" %% "slick-pg_date2" % V.slickPg,
    "com.github.tminglei" %% "slick-pg_play-json" % V.slickPg,
    "com.typesafe.slick" %% "slick-hikaricp" % V.slick,
    "com.vmunier" %% "scalajs-scripts" % V.playScripts,
    "com.lihaoyi" %% "scalatags" % V.scalatags,
    "com.github.japgolly.scalacss" %% "core" % V.scalaCSS,
    "org.webjars" %% "webjars-play" % V.webJar,
    "org.webjars" % "font-awesome" % "4.6.3" % Provided,
    "org.webjars" % "Semantic-UI" % jsV.semanticUI % Provided,
    "org.webjars" % "jquery" % jsV.jQuery,
    "com.typesafe.akka" %% "akka-actor" % V.akka,
    "com.typesafe.akka" % "akka-stream_2.11" % V.akka,
    "com.typesafe.akka" % "akka-testkit_2.11" % V.akka % Test,
    "com.mohiva" %% "play-silhouette" % V.silhouette,
    "com.mohiva" %% "play-silhouette-password-bcrypt" % V.silhouette,
    "com.mohiva" %% "play-silhouette-crypto-jca" % V.silhouette,
    "com.mohiva" %% "play-silhouette-persistence" % V.silhouette,
    "com.mohiva" %% "play-silhouette-testkit" % V.silhouette % Test,
    "org.scalatestplus.play" %% "scalatestplus-play" % V.scalatestPlay % Test,
    "net.codingwell" %% "scala-guice" % V.scalaGuice
  ))

  val clientDependencies = Def.setting(Seq(
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