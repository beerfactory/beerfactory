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
import scalacss.Defaults._
import scalacss.ScalaCssReact._
import scalacss.defaults.Exports.StyleSheet

object LoginForm {
  case class Props(router: RouterCtl[Page],
                   proxy: ModelProxy[UserModel],
                   onSubmit: (String, String) ⇒ Callback)

  case class State(authData: String, password: String)

  object Styles extends StyleSheet.Inline {
    import dsl._

    val loginFormButton   = style(addClassNames("ui", "fluid", "large", "blue", "button"))
    val bottomFormMessage = style(addClassNames("ui", "bottom", "attached", "green", "message"))
    val passwordFieldDesc = style(textAlign.right)
  }
  Styles.addToDocument()

  class Backend(scope: BackendScope[Props, State]) {
    def handleClick(props: Props, state: State)(e: ReactEventI) = {
      e.preventDefaultCB >> props.onSubmit(state.authData, state.password)
    }

    def handleChangeAuthData(event: ReactEventI) = {
      val text = event.target.value
      scope.modState(s ⇒ s.copy(authData = text))
    }

    def handleChangePassword(event: ReactEventI) = {
      val text = event.target.value
      scope.modState(s ⇒ s.copy(password = text))
    }

    def render(props: Props, s: State) = {
      div(cls := "column",
          form(cls := "ui column large form attached segment",
               InputField(
                 InputField.Props(fieldName = "authData",
                                  inputType = "email",
                                  required = true,
                                  placeholder = Some("Email"),
                                  icon = Some("user"),
                                  onChange = handleChangeAuthData)),
               InputField(
                 InputField.Props(fieldName = "password",
                                  required = true,
                                  placeholder = Some("Password"),
                                  icon = Some("lock"),
                                  inputType = "password",
                                  descriptionStyle = Styles.passwordFieldDesc,
                                  description = Some(a(href := "#", "Forgot password ?")),
                                  onChange = handleChangePassword)),
               button(Styles.loginFormButton, onClick ==> handleClick(props, s), "Login")),
          div(Styles.bottomFormMessage,
              i(cls := "add user icon"),
              "New to Beerfactory? ",
              props.router.link(Register)("Create an account.")))
    }
  }

  private val component =
    ReactComponentB[Props]("LoginForm")
      .initialState_P(p ⇒ State("", ""))
      .renderBackend[Backend]
      .build

  def apply(props: Props) = component(props)
}
