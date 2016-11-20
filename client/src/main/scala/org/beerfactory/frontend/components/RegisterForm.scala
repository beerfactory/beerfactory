package org.beerfactory.frontend.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.all._
import org.beerfactory.frontend.GlobalStyles
import org.beerfactory.frontend.pages.Page
import org.beerfactory.frontend.utils.Forms.FormError
import org.beerfactory.shared.utils.Validators._
import org.scalactic.Good
import org.scalactic.Accumulation._
import slogging.LazyLogging

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

  case class FormData(userName: String,
                      email: String,
                      confirmEmail: String,
                      password: String,
                      firstName: Option[String] = None,
                      lastName: Option[String] = None)

  case class Props(router: RouterCtl[Page],
                   onSubmit: (FormData) ⇒ Callback,
                   errorMessage: Option[String])

  case class State(formData: FormData, formError: Option[FormError])

  class Backend(scope: BackendScope[Props, State]) extends LazyLogging {

    def validateForm(formData: FormData): Either[FormError, FormData] = {
      val usernameValidation = Good(formData.userName) when notEmpty(
          ("username", "username must not be empty"))
      val emailValidation = Good(formData.email) when (notEmpty(
          ("email", "email must not be empty")), validEmailAddress(
          ("email", "Invalid e-mail address")))
      val confirmEmailValidation = Good(formData.confirmEmail) when (notEmpty(
          ("confirm_email", "email must not be empty")), validEquals(
          ("confirm_email", "Email confirmation doesn't match"),
          formData.email))
      val passwordValidation = Good(formData.password) when notEmpty(
          ("password", "password must not be empty"))

      //Errors are expected to be a tuple containing the name of the field in error and an error message
      withGood(usernameValidation, emailValidation, confirmEmailValidation, passwordValidation) {
        FormData(_, _, _, _, formData.firstName, formData.lastName)
      }.fold(
        formData => Right(formData),
        errors =>
          Left(
            FormError(headerMessage = "Invalid form input data",
                      errorFields = errors.groupBy(error => error._1).keySet,
                      errorMessages = errors.map(v => v._2)))
      )
    }

    def handleClick(props: Props, state: State)(e: ReactEventI) = {
      e.preventDefaultCB >> validateForm(state.formData).fold(
        fe => scope.modState(s => s.copy(formError = Some(fe))),
        fd => props.onSubmit(fd)
      )
    }

    def handleChangeField(fieldName: String, state: State)(event: ReactEventI): Callback = {
      val text = event.target.value
      val newFormData = fieldName match {
        case "username"      ⇒ state.formData.copy(userName = text)
        case "email"         ⇒ state.formData.copy(email = text)
        case "confirm_email" ⇒ state.formData.copy(confirmEmail = text)
        case "password"      ⇒ state.formData.copy(password = text)
        case "first_name"    ⇒ state.formData.copy(firstName = Some(text))
        case "last_name"     ⇒ state.formData.copy(lastName = Some(text))
        case other ⇒
          logger.error("Unmatched event received for field name {}", other)
          state.formData
      }
      scope.modState(s ⇒ s.copy(formData = newFormData, formError = None))
    }

    def render(props: Props, state: State) =
      div(Styles.largeForm,
          cls := "column",
          if (state.formError.isDefined) {
            div(GlobalStyles.leftAlignedErrorMessage,
                div(cls := "header", state.formError.get.headerMessage),
                ul(cls := "list", for (v <- state.formError.get.errorMessages) yield li(v)))
          } else {
            div()
          },
          h3(cls := "ui top attached message", "Create your personal account"),
          form(
            cls := "ui column large form attached segment",
            InputField(
              InputField.Props(fieldName = "username",
                               required = true,
                               label = Some("Username"),
                               error = state.formError.isDefined && state.formError.get.errorFields
                                   .contains("username"),
                               onChange = handleChangeField("username", state))),
            InputField(
              InputField.Props(fieldName = "email",
                               inputType = "email",
                               required = true,
                               label = Some("E-mail address"),
                               error = state.formError.isDefined && state.formError.get.errorFields
                                   .contains("email"),
                               onChange = handleChangeField("email", state))),
            InputField(
              InputField.Props(fieldName = "confirm_email",
                               inputType = "email",
                               required = true,
                               label = Some("Confirm E-mail address"),
                               error = state.formError.isDefined && state.formError.get.errorFields
                                   .contains("confirm_email"),
                               onChange = handleChangeField("confirm_email", state))),
            InputField(InputField
              .Props(fieldName = "password",
                     inputType = "password",
                     required = true,
                     label = Some("Password"),
                     error = state.formError.isDefined && state.formError.get.errorFields.contains(
                         "password"),
                     onChange = handleChangeField("password", state))),
            TwoInputsField(
              TwoInputsField.Props(Some("Name"),
                                   (InputField.Props(
                                      fieldName = "first_name",
                                      placeholder = Some("First name"),
                                      error = state.formError.isDefined && state.formError.get.errorFields
                                          .contains("first_name"),
                                      onChange = handleChangeField("first_name", state)),
                                    InputField.Props(
                                      fieldName = "last_name",
                                      placeholder = Some("Last name"),
                                      error = state.formError.isDefined && state.formError.get.errorFields
                                          .contains("last_name"),
                                      onChange = handleChangeField("last_name", state))))),
            button(Styles.submitButton, onClick ==> handleClick(props, state), "Create an account")
          ))
  }

  private val component =
    ReactComponentB[Props]("RegisterForm")
      .initialState(State(FormData("", "", "", ""), None))
      .renderBackend[Backend]
      .componentWillReceiveProps(
        update =>
          if (update.nextProps.errorMessage.isDefined)
            update.$.modState(state =>
              state.copy(formError =
                Some(FormError(update.nextProps.errorMessage.get, Set.empty, Seq.empty))))
          else
            Callback.empty)
      .build

  def apply(props: Props) = component(props)
}
