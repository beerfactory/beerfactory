package org.beerfactory.shared.components

import japgolly.scalajs.react.{ReactComponentB, ReactElement}
import japgolly.scalajs.react.vdom.all._
import scalacss.Defaults._

/**
  * Created by njouanin on 08/11/16.
  */
object Commons {

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
