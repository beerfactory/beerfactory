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
import org.beerfactory.shared.api.UserLoginRequest
import org.beerfactory.shared.utils.Validators._
import org.scalactic.Accumulation._
import org.scalactic._

import scala.collection.mutable
import scalacss.Defaults._
import scalacss.ScalaCssReact._
import scalacss.defaults.Exports.StyleSheet

object LoginForm {

  case class FormData(authData: String, password: String)
  case class FormError(generalErrorMessage: String, errorFields: Set[String], errorMessages: Seq[String])

  case class Props(router: RouterCtl[Page],
                   proxy: ModelProxy[UserModel],
                   onSubmit: (String, String) ⇒ Callback,
                   errors: Map[String, String])

  case class State(formData:FormData, errorFields : Set[String], generalErrorMessage: Option[String], errorMessages: Seq[String])

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
      val validations = for {
        authData    ← Good(formData.authData) when notEmpty(("authData", "authData empty"))
        password ← Good(formData.password) when notEmpty(("password", "password empty"))
      } yield FormData(authData, password)

      validations.fold(
        formData => Right(formData),
        errors => Left(FormError(generalErrorMessage = "Invalid fields data",
          errorFields = errors.groupBy(error => error._1).keySet,
          errorMessages = errors.map(v => v._2)))
      )
    }

    def handleClick(props: Props, state: State)(e: ReactEventI) = {
      e.preventDefaultCB >> validateForm(state.formData).fold(
          formError => scope.modState(s => s.copy(errorFields = formError.errorFields)),
          formData => props.onSubmit(formData.authData, formData.password)
        )
    }

    def handleChangeAuthData(state: State)(event: ReactEventI) = {
      val text = event.target.value
      scope.modState(s ⇒ s.copy(formData=s.formData.copy(authData = text)))
    }

    def handleChangePassword(state: State)(event: ReactEventI) = {
      val text = event.target.value
      scope.modState(s ⇒ s.copy(formData=s.formData.copy(password = text)))
    }

    def render(props: Props, s: State) = {
      div(cls := "column",
          if (!props.errors.isEmpty)
            div(Styles.leftAlignedErrorMessage, div(cls := "header", "Form errors"), ul(cls:="list", for((k,v) <- props.errors) yield li(v)))
          else
            div(),
          form(cls := "ui column large form attached segment",
               div(cls := "ui error message", div(cls := "header", "Header"), p("Error")),
               InputField(
                 InputField.Props(fieldName = "authData",
                                  inputType = "email",
                                  required = true,
                                  error = s.errorFields.contains("authData"),
                                  placeholder = Some("Email"),
                                  icon = Some("user"),
                                  onChange = handleChangeAuthData(s))),
               InputField(
                 InputField.Props(fieldName = "password",
                                  required = true,
                                  error = s.errorFields.contains("password"),
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
      .initialState_P(p ⇒ State(FormData("", ""), Set.empty, None, Seq.empty))
      .renderBackend[Backend]
      .build

  def apply(props: Props) = component(props)
}
