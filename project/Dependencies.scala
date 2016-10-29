import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

object Dependencies {
  // Versions
  object V {
    val akka          = "2.4.11"
    val scalactic     = "2.2.6"
    val scalatest     = "2.2.6"
    val scalatestPlay = "1.5.1"
    val scalajsReact  = "0.11.2"
    val scalaCSS      = "0.5.0"
    val diode         = "1.0.0"
    val h2            = "1.4.192"
    val akkaHttpJson  = "1.9.0"
    val scalatags     = "0.6.0"
    val playScripts   = "1.0.0"
    val silhouette    = "4.0.0"
    val scalaGuice    = "4.1.0"
    val webJar        = "2.5.0"
    val ficus         = "1.3.0"
    val playSlick     = "2.0.0"
    val playMailer    = "5.0.0"
    val play          = "2.5.8"
  }

  object jsV {
    val react      = "15.3.1"
    val semanticUI = "2.2.2"
    val jQuery     = "3.1.0"
  }

  val commonDependencies = Def.setting(
    Seq(
      "org.scalactic" %% "scalactic" % V.scalactic,
      "org.scalatest" %% "scalatest" % V.scalatest % "test"
    ))

  val sharedDependencies = Def.setting(
    Seq(
      "com.typesafe.play" %% "play-json" % V.play
    ))

  val serverDependencies = Def.setting(
    Seq(
      "com.typesafe.play"            %% "play-slick"                      % V.playSlick,
      "com.typesafe.play"            %% "play-slick-evolutions"           % V.playSlick,
      "com.typesafe.play"            %% "play-mailer"                     % V.playMailer,
      "com.h2database"               % "h2"                               % V.h2,
      "org.postgresql"               % "postgresql"                       % "9.4.1209",
      "com.vmunier"                  %% "scalajs-scripts"                 % V.playScripts,
      "com.lihaoyi"                  %% "scalatags"                       % V.scalatags,
      "com.github.japgolly.scalacss" %% "core"                            % V.scalaCSS,
      "org.webjars"                  % "font-awesome"                     % "4.6.3" % Provided,
      "org.webjars"                  % "Semantic-UI"                      % jsV.semanticUI % Provided,
      "org.webjars"                  % "jquery"                           % jsV.jQuery,
      "com.mohiva"                   %% "play-silhouette"                 % V.silhouette,
      "com.mohiva"                   %% "play-silhouette-password-bcrypt" % V.silhouette,
      "com.mohiva"                   %% "play-silhouette-crypto-jca"      % V.silhouette,
      "com.mohiva"                   %% "play-silhouette-persistence"     % V.silhouette,
      "com.mohiva"                   %% "play-silhouette-testkit"         % V.silhouette % Test,
      "org.scalatestplus.play"       %% "scalatestplus-play"              % V.scalatestPlay % Test,
      "com.typesafe.akka"            %% "akka-testkit"                    % V.akka % Test,
      "net.codingwell"               %% "scala-guice"                     % V.scalaGuice,
      "com.iheart"                   %% "ficus"                           % V.ficus
    ))

  val clientDependencies = Def.setting(
    Seq(
      "com.github.japgolly.scalajs-react" %%% "core"        % V.scalajsReact,
      "com.github.japgolly.scalajs-react" %%% "extra"       % V.scalajsReact,
      "com.github.japgolly.scalacss"      %%% "ext-react"   % V.scalaCSS,
      "me.chrons"                         %%% "diode"       % V.diode,
      "me.chrons"                         %%% "diode-react" % V.diode,
      "org.scalatest"                     %%% "scalatest"   % "3.0.0" % "test"
    ))

  val jsDependencies = Seq(
    "org.webjars.bower" % "react"       % jsV.react / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React",
    "org.webjars.bower" % "react"       % jsV.react / "react-dom.js" minified "react-dom.min.js" dependsOn "react-with-addons.js" commonJSName "ReactDOM",
    "org.webjars.bower" % "react"       % jsV.react / "react-dom-server.js" minified "react-dom-server.min.js" dependsOn "react-dom.js" commonJSName "ReactDOMServer",
    "org.webjars"       % "jquery"      % jsV.jQuery / "jquery.js" minified "jquery.min.js",
    "org.webjars"       % "Semantic-UI" % jsV.semanticUI / "semantic.js" minified "semantic.min.js" dependsOn "jquery.js"
  )
}
