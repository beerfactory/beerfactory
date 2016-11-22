/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.shared.components

import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react._

object TwoInputsField {
  case class Props(label: Option[String], twoProps: (InputField.Props, InputField.Props))

  def component =
    ReactComponentB[Props]("InputField")
      .render_P(props ⇒ {
        div(cls := "field",
            props.label.map(l ⇒ label(l)),
            div(cls := "two fields", InputField(props.twoProps._1), InputField(props.twoProps._2)))
      })
      .build

  def apply(props: Props) = component(props)
}
