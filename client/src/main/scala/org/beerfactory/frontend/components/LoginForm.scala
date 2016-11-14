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
import org.scalactic.Accumulation._
import org.scalactic._

import scalacss.Defaults._
import scalacss.ScalaCssReact._
import scalacss.defaults.Exports.StyleSheet

object LoginForm {
  case class Props(router: RouterCtl[Page],
                   proxy: ModelProxy[UserModel],
                   onSubmit: (String, String) ⇒ Callback,
                   errors: Map[String, String])

  case class State(authData: String, password: String, errorFields : Set[String], errorMessageHeader: Option[String], errorMessages: Seq[String])

  object Styles extends StyleSheet.Inline {
    import dsl._

    val loginFormButton         = style(addClassNames("ui", "fluid", "large", "blue", "button"))
    val bottomFormMessage       = style(addClassNames("ui", "bottom", "attached", "green", "message"))
    val passwordFieldDesc       = style(textAlign.right)
    val leftAlignedErrorMessage = style(addClassNames("ui", "error", "message"), textAlign.left)
  }
  Styles.addToDocument()

  class Backend(scope: BackendScope[Props, State]) {

    def validateFields(state: State): Callback = {
      def validateAuthData(authData: String): Callback = {
        val trimmed = authData.trim
        if (trimmed.isEmpty)
          scope.modState(s ⇒ s.copy(errorFields = s.errorFields + "authData", errorMessageHeader = Some("Invalid input")))
        else
          Callback.empty
      }

      def validatePassword(password: String): Callback = {
        val trimmed = password.trim
        if (trimmed.isEmpty)
          scope.modState(s ⇒ s.copy(errorFields = s.errorFields + "password", errorMessageHeader = Some("Invalid input")))
        else
          Callback.empty
      }

      scope.modState(s ⇒ s.copy(errorFields = Set.empty)) >> validateAuthData(state.authData) >> validatePassword(state.password) >> Callback.log(s"validate=$state")
    }

    def handleClick(props: Props, state: State)(e: ReactEventI) = {
      e.preventDefaultCB >> validateFields(state) >> scope.state.flatMap{ s =>
        println(s"${s}")
        if(s.errorFields.isEmpty) props.onSubmit(s.authData, s.password) else Callback.empty
      }
    }

    def handleChangeAuthData(state: State)(event: ReactEventI) = {
      val text = event.target.value
      scope.modState(s ⇒ s.copy(authData = text))// >> validateFields(state)
    }

    def handleChangePassword(state: State)(event: ReactEventI) = {
      val text = event.target.value
      scope.modState(s ⇒ s.copy(password = text))// >> validateFields(state)
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
      .initialState_P(p ⇒ State("", "", Set.empty, None, Seq.empty))
      .renderBackend[Backend]
      .build

  def apply(props: Props) = component(props)
}
