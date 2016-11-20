/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.frontend.components

import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.all._
import org.beerfactory.frontend.components.Commons._
import org.beerfactory.frontend.pages.{Page, Register}
import org.beerfactory.frontend.state.UserModel
import org.beerfactory.frontend.utils.AjaxApiFacade
import org.beerfactory.frontend.utils.Forms.FormError
import org.beerfactory.shared.utils.Validators._
import org.scalactic.Accumulation._
import org.scalactic._

import scalacss.Defaults._
import scalacss.ScalaCssReact._
import scalacss.defaults.Exports.StyleSheet

object LoginForm {

  case class FormData(authData: String, password: String)

  case class Props(router: RouterCtl[Page],
                   proxy: ModelProxy[UserModel],
                   onSubmit: (String, String) ⇒ Callback,
                   errorMessage: Option[String])

  case class State(formData: FormData, formError: Option[FormError])

  object Styles extends StyleSheet.Inline {
    import dsl._

    val loginFormButton         = style(addClassNames("ui", "fluid", "large", "blue", "button"))
    val bottomFormMessage       = style(addClassNames("ui", "bottom", "attached", "green", "message"))
    val passwordFieldDesc       = style(textAlign.right)
    val leftAlignedErrorMessage = style(addClassNames("ui", "error", "message"), textAlign.left)
  }
  Styles.addToDocument()

  class Backend(scope: BackendScope[Props, State]) {

    def validateForm(formData: FormData): Either[FormError, FormData] = {
      val authDataValidation = Good(formData.authData) when notEmpty(
          ("authData", "e-mail must not be empty"))
      val passwordValidation = Good(formData.password) when notEmpty(
          ("password", "password must not be empty"))

      //Errors are expected to be a tuple containing the name of the field in error and an error message
      withGood(authDataValidation, passwordValidation) { FormData(_, _) }.fold(
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
        fd => props.onSubmit(fd.authData, fd.password)
      )
    }

    def handleChangeAuthData(state: State)(event: ReactEventI) = {
      val text = event.target.value
      scope.modState(s ⇒ s.copy(formData = s.formData.copy(authData = text), formError = None))
    }

    def handleChangePassword(state: State)(event: ReactEventI) = {
      val text = event.target.value
      scope.modState(s ⇒ s.copy(formData = s.formData.copy(password = text), formError = None))
    }

    def render(props: Props, s: State) = {
      div(
        cls := "column",
        if (s.formError.isDefined) {
          div(Styles.leftAlignedErrorMessage,
              div(cls := "header", s.formError.get.headerMessage),
              ul(cls := "list", for (v <- s.formError.get.errorMessages) yield li(v)))
        } else {
          div()
        },
        form(cls := "ui column large form attached segment",
             InputField(
               InputField.Props(
                 fieldName = "authData",
                 inputType = "email",
                 required = true,
                 error = s.formError.isDefined && s.formError.get.errorFields.contains("authData"),
                 placeholder = Some("Email"),
                 icon = Some("user"),
                 onChange = handleChangeAuthData(s))),
             InputField(
               InputField.Props(
                 fieldName = "password",
                 required = true,
                 error = s.formError.isDefined && s.formError.get.errorFields.contains("password"),
                 placeholder = Some("Password"),
                 icon = Some("lock"),
                 inputType = "password",
                 descriptionStyle = Styles.passwordFieldDesc,
                 description = Some(a(href := "#", "Forgot password ?")),
                 onChange = handleChangePassword(s))),
             button(Styles.loginFormButton, onClick ==> handleClick(props, s), "Login")),
        div(Styles.bottomFormMessage,
            i(cls := "add user icon"),
            "New to Beerfactory? ",
            props.router.link(Register)("Create an account.")))
    }
  }

  private val component =
    ReactComponentB[Props]("LoginForm")
      .initialState_P(p ⇒
        State(FormData("", ""), p.errorMessage match {
          case Some(error) ⇒ Some(FormError(error, Set.empty, Seq.empty))
          case _           ⇒ None
        }))
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
