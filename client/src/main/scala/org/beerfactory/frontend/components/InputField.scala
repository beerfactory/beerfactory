/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.frontend.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.all._
import org.beerfactory.frontend.components.Commons.CommonStyles

import scalacss.ScalaCssReact._
import scalacss.internal.StyleA

object InputField {
  case class Props(fieldName: String,
                   required: Boolean,
                   inputType: String = "text",
                   onChange: ReactEventI ⇒ Callback,
                   label: Option[String] = None,
                   placeholder: Option[String] = None,
                   description: Option[TagMod] = None,
                   descriptionStyle: StyleA = CommonStyles.inputComment,
                   icon: Option[String] = None)
  case class State(isValid: Boolean)

  class Backend(scope: BackendScope[Props, State]) {
    def handleOnChange(event: ReactEventI) = {
      //TODO: Implement field validation here
      scope.props.flatMap(p ⇒ p.onChange(event))
    }

    def render(p: Props, state: State) = {
      val fieldClass = if (p.required) "required field" else "field"
      val inputClass = if (p.icon.isDefined) "ui left icon input" else "ui input"

      div(cls := fieldClass,
          div(cls := inputClass,
              p.label.map(l ⇒ label(l)),
              input(`type` := p.inputType,
                    name := p.fieldName,
                    placeholder := p.placeholder.getOrElse[String](""),
                    onChange ==> handleOnChange),
              p.icon.map(iconName ⇒ i(cls := iconName + " icon"))),
          p.description.map(desc => div(p.descriptionStyle, small(desc))))

    }
  }
  private val component =
    ReactComponentB[Props]("InputField")
      .initialState_P(p ⇒ State(true))
      .renderBackend[Backend]
      .build

  def apply(props: Props) = component(props)
}
