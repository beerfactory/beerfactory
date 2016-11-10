package org.beerfactory.frontend.pages

import diode.react.ModelProxy
import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react.extra.router.RouterCtl
import org.beerfactory.frontend.state.UserModel
import japgolly.scalajs.react.vdom.all._
import org.beerfactory.frontend.components.Commons._

import scalacss.ScalaCssReact._
import scalacss.Defaults._

/**
  * Created by njouanin on 07/11/16.
  */
object LoginPage {

  case class Props(router: RouterCtl[Page], proxy: ModelProxy[UserModel])

  object Styles extends StyleSheet.Inline {
    import dsl._

    val loginFormButton   = style(addClassNames("ui", "fluid", "large", "blue", "button"))
    val bottomFormMessage = style(addClassNames("ui", "bottom", "attached", "green", "message"))
    val passwordFieldDesc = style(textAlign.right)
  }
  Styles.addToDocument()

  private val LoginForm = ReactComponentB[Props]("LoginPage").render_P { props =>
    div(cls := "column",
        form(cls := "ui column large form attached segment",
             InputField(
               InputFieldProps(fieldName = "username",
                               required = true,
                               placeholder = Some("Email"),
                               icon = Some("user"))),
             InputField(
               InputFieldProps(fieldName = "password",
                               required = true,
                               placeholder = Some("Password"),
                               icon = Some("lock"),
                               descriptionStyle = Styles.passwordFieldDesc,
                               description = Some(a(href := "#", "Forgot password ?")))),
             div(Styles.loginFormButton, "Login")),
        div(Styles.bottomFormMessage,
            i(cls := "add user icon"),
            "New to Beerfactory? ",
            props.router.link(Register)("Create an account.")))
  }.build

  private val component =
    ReactComponentB[Props]("LoginPage").render_P { props =>
      // format: off
      div(cls := "ui three column centered grid",
        GridRow(H1Header("Login to Beerfactory")),
        GridRow(LoginForm(props))
      )
      // format: on
    }.build

  def apply(router: RouterCtl[Page], proxy: ModelProxy[UserModel]) =
    component(Props(router, proxy))
}
