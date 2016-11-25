import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

object Dependencies {
  // Versions
  object V {
    val akka         = "2.4.14"
    val akkaHttp     = "10.0.0"
    val scalactic    = "3.0.0"
    val scalatest    = "3.0.0"
    val scalajsReact = "0.11.3"
    val scalaCSS     = "0.5.1"
    val diode        = "1.1.0"
    val h2           = "1.4.193"
    val scalatags    = "0.6.2"
    val webJar       = "2.5.0"
    val ficus        = "1.3.3"
    val slogging     = "0.5.2"
    val circe        = "0.6.1"
  }

  object jsV {
    val react      = "15.3.2"
    val semanticUI = "2.2.6"
    val jQuery     = "3.1.1"
    val jsJoda     = "1.1.8"
  }

  val commonDependencies = Def.setting(
    Seq(
      "org.scalactic" %%% "scalactic" % V.scalactic,
      "org.scalatest" %%% "scalatest" % V.scalatest % "test"
    ))

  val sharedDependencies = Def.setting(
    Seq(
      "com.github.japgolly.scalajs-react" %%% "core"      % V.scalajsReact,
      "com.github.japgolly.scalajs-react" %%% "extra"     % V.scalajsReact,
      "com.github.japgolly.scalajs-react" %%% "test"      % V.scalajsReact % "test",
      "com.github.japgolly.scalacss"      %%% "ext-react" % V.scalaCSS,
      "biz.enef"                          %%% "slogging"  % V.slogging,
      "org.scalactic"                     %%% "scalactic" % V.scalactic
    ))

  val serverDependencies = Def.setting(
    Seq(
      "com.h2database"               % "h2"            % V.h2,
      "org.postgresql"               % "postgresql"    % "9.4.1209",
      "com.lihaoyi"                  %% "scalatags"    % V.scalatags,
      "com.github.japgolly.scalacss" %% "core"         % V.scalaCSS,
      "org.webjars"                  % "font-awesome"  % "4.6.3" % Provided,
      "org.webjars.bower"            % "css-avatars"   % "0.4.0" % Provided,
      "org.webjars.bower"            % "semantic-ui"   % jsV.semanticUI % Provided,
      "com.typesafe.akka"            %% "akka-testkit" % V.akka % Test,
      "com.iheart"                   %% "ficus"        % V.ficus
    ))

  val clientDependencies = Def.setting(
    Seq(
      "com.github.japgolly.scalajs-react" %%% "core"                        % V.scalajsReact,
      "com.github.japgolly.scalajs-react" %%% "extra"                       % V.scalajsReact,
      "com.github.japgolly.scalajs-react" %%% "test"                        % V.scalajsReact % "test",
      "com.github.japgolly.scalacss"      %%% "ext-react"                   % V.scalaCSS,
      "me.chrons"                         %%% "diode"                       % V.diode,
      "me.chrons"                         %%% "diode-react"                 % V.diode,
      "com.zoepepper"                     %%% "scalajs-jsjoda"              % "1.0.5",
      "com.zoepepper"                     %%% "scalajs-jsjoda-as-java-time" % "1.0.5",
      "biz.enef"                          %%% "slogging"                    % V.slogging,
      "org.scalactic"                     %%% "scalactic"                   % V.scalactic,
      "org.scalatest"                     %%% "scalatest"                   % V.scalatest % "test"
    ) ++ Seq(
      "io.circe" %%% "circe-core",
      "io.circe" %%% "circe-generic",
      "io.circe" %%% "circe-parser"
    ).map(_ % V.circe)
  )

  val jsDependencies = Seq(
    "org.webjars.bower" % "react"       % jsV.react / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React",
    "org.webjars.bower" % "react"       % jsV.react / "react-dom.js" minified "react-dom.min.js" dependsOn "react-with-addons.js" commonJSName "ReactDOM",
    "org.webjars.bower" % "react"       % jsV.react / "react-dom-server.js" minified "react-dom-server.min.js" dependsOn "react-dom.js" commonJSName "ReactDOMServer",
    "org.webjars.bower" % "jquery"      % jsV.jQuery / "dist/jquery.js" minified "dist/jquery.min.js",
    "org.webjars.bower" % "semantic-ui" % jsV.semanticUI / "dist/semantic.js" minified "dist/semantic.min.js" dependsOn "dist/jquery.js",
    "org.webjars.npm"   % "js-joda"     % jsV.jsJoda / "dist/js-joda.js" minified "dist/js-joda.min.js"
  )
}
