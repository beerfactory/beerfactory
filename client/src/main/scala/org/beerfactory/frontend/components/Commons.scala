package org.beerfactory.frontend.components

import japgolly.scalajs.react.{ReactComponentB, ReactElement}
import japgolly.scalajs.react.vdom.all._

/**
  * Created by njouanin on 08/11/16.
  */
object Commons {
  val H1Header = ReactComponentB[String]("H1Header")
    .render_P(title => h1(cls := "cu centered header", title))
    .build

  val GridRow =
    ReactComponentB[ReactElement]("GridRow").render_P(child => div(cls := "row", child))

  case class InputFieldProps(inputType: String, required: Boolean, icon: Option[String] = None)

  val InputField = ReactComponentB[InputFieldProps]("InputField").render_P { p =>
    val fieldClass = if (p.required) "required field" else "field"
    val inputClass = if (p.icon.isDefined) "ui left icon input" else "ui input"
    div(cls := fieldClass, div(cls := inputClass, ""))
  }.build

}
