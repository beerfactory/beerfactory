package org.beerfactory.frontend.components

import japgolly.scalajs.react.{ReactComponentB, ReactDOM, ReactElement}
import japgolly.scalajs.react.vdom.all._
import org.scalajs.dom.Element

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
    .render_P(title => h1(cls := "cu centered header", title))
    .build

  val GridRow =
    ReactComponentB[ReactElement]("GridRow").render_P(child => div(cls := "row", child)).build

  case class InputFieldProps(fieldName: String,
                             required: Boolean,
                             inputType: String = "text",
                             label: Option[String] = None,
                             placeholder: Option[String] = None,
                             description: Option[TagMod] = None,
                             descriptionStyle: StyleA = CommonStyles.inputComment,
                             icon: Option[String] = None)

  val InputField = ReactComponentB[InputFieldProps]("InputField").render_P { p =>
    val fieldClass = if (p.required) "required field" else "field"
    val inputClass = if (p.icon.isDefined) "ui left icon input" else "ui input"
    // format: off
    div(cls := fieldClass,
      div(cls := inputClass,
        p.label.map(l ⇒ label(l)),
        input(`type` := p.inputType, name:=p.fieldName, placeholder := p.placeholder.getOrElse[String]("")),
        p.icon.map(iconName ⇒ i(cls := iconName + " icon"))
      ),
        p.description.map(desc =>
          div(p.descriptionStyle,
            small(desc)
          )
      )
    )
    // format: on
  }.build

}
