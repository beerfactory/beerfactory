package org.beerfactory.frontend.components

import japgolly.scalajs.react.{Callback, ReactComponentB, ReactDOM, ReactElement}
import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react._

import scalacss.defaults.Exports.StyleSheet
import scalacss.ScalaCssReact._
import scalacss.internal.{StyleA, StyleS}
import scalacss.Defaults._

/**
  * Created by njouanin on 08/11/16.
  */
object Commons {

  object CommonStyles extends StyleSheet.Inline {
    import dsl._

    val inputComment = style(color(c"#767676"))

  }
  CommonStyles.addToDocument()

  val H1Header = ReactComponentB[String]("H1Header")
    .render_P(title => h1(cls := "ui centered header", title))
    .build

  val GridRow =
    ReactComponentB[ReactElement]("GridRow").render_P(child => div(cls := "row", child)).build

  val GridCenteredRow =
    ReactComponentB[ReactElement]("GridCenteredRow")
      .render_P(child => div(cls := "centered row", child))
      .build
}
