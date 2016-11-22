/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.shared.components

import japgolly.scalajs.react.{Callback, ReactComponentB}
import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react._

import scalacss.ScalaCssReact._
import scalacss.Defaults._

object InputField {

  object Styles extends StyleSheet.Inline {
    import dsl._

    val inputComment = style(color(c"#767676"))

  }
  Styles.addToDocument()

  case class Props(fieldName: String,
                   required: Boolean = false,
                   error: Boolean = false,
                   inputType: String = "text",
                   onChange: ReactEventI ⇒ Callback,
                   label: Option[String] = None,
                   placeholder: Option[String] = None,
                   description: Option[TagMod] = None,
                   descriptionStyle: StyleA = Styles.inputComment,
                   icon: Option[String] = None)

  private val component =
    ReactComponentB[Props]("InputField")
      .render_P(p ⇒ {
        val fieldClass = Seq("required", "error", "field")
          .filter(attr ⇒
            attr match {
              case "required" ⇒ if (p.required) true else false
              case "error"    ⇒ if (p.error) true else false
              case _          ⇒ true
          })
          .mkString(" ")
        //val fieldClass = if (p.required) "required field" else "field"
        val inputClass = if (p.icon.isDefined) "ui left icon input" else "ui input"

        div(cls := fieldClass,
            p.label.map(l ⇒ label(l)),
            div(cls := inputClass,
                input(`type` := p.inputType,
                      name := p.fieldName,
                      placeholder := p.placeholder.getOrElse[String](""),
                      onChange ==> p.onChange),
                p.icon.map(iconName ⇒ i(cls := iconName + " icon"))),
            p.description.map(desc => div(p.descriptionStyle, small(desc))))

      })
      .build

  def apply(props: Props) = component(props)
}
