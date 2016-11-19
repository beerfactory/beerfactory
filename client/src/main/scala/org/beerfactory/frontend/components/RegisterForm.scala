package org.beerfactory.frontend.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.all._
import org.beerfactory.frontend.pages.Page
import org.beerfactory.frontend.utils.Forms.FormError

import scalacss.Defaults._
import scalacss.ScalaCssReact._
import scalacss.defaults.Exports.StyleSheet

/**
  * Created by njouanin on 18/11/16.
  */
object RegisterForm {
  object Styles extends StyleSheet.Inline {
    import dsl._

    val largeForm    = style(width(700 px).important, margin.auto)
    val submitButton = style(addClassNames("ui", "green", "button"))

  }
  Styles.addToDocument()

  case class Props(router: RouterCtl[Page])

  case class State(formError: Option[FormError])

  class Backend(scope: BackendScope[Props, State]) {

    def handleChangeUserName(state: State)(event: ReactEventI): Callback = ???

    def render(props: Props, state: State) =
      div(
        Styles.largeForm,
        cls := "column",
        h3(cls := "ui top attached message", "Create your personal account"),
        form(
          cls := "ui column large form attached segment",
          InputField(
            InputField.Props(
              fieldName = "username",
              required = true,
              label = Some("Username"),
              error = state.formError.isDefined && state.formError.get.errorFields
                  .contains("username"),
              onChange = handleChangeUserName(state))),
          InputField(
            InputField
              .Props(fieldName = "email",
                     inputType = "email",
                     required = true,
                     label = Some("E-mail address"),
                     error = state.formError.isDefined && state.formError.get.errorFields.contains(
                         "email"),
                     onChange = handleChangeUserName(state))),
          InputField(InputField
            .Props(fieldName = "confirm_email",
                   inputType = "email",
                   required = true,
                   label = Some("Confirm E-mail address"),
                   error = state.formError.isDefined && state.formError.get.errorFields
                       .contains("confirm_email"),
                   onChange = handleChangeUserName(state))),
          InputField(
            InputField.Props(fieldName = "password",
                             inputType = "password",
                             required = true,
                             label = Some("Password"),
                             error = state.formError.isDefined && state.formError.get.errorFields
                                 .contains("password"),
                             onChange = handleChangeUserName(state))),
          TwoInputsField(
            TwoInputsField.Props(
              Some("Name"),
              (InputField.Props(fieldName = "first_name",
                                placeholder = Some("First name"),
                                error = state.formError.isDefined && state.formError.get.errorFields
                                    .contains("first_name"),
                                onChange = handleChangeUserName(state)),
               InputField.Props(
                 fieldName = "last_name",
                 placeholder = Some("Last name"),
                 error = state.formError.isDefined && state.formError.get.errorFields
                     .contains("last_name"),
                 onChange = handleChangeUserName(state))))),
          button(Styles.submitButton, "Create an account")
        ))
  }

  private val component =
    ReactComponentB[Props]("RegisterForm").initialState(State(None)).renderBackend[Backend].build

  def apply(props: Props) = component(props)
}
