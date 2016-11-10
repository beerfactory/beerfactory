package org.beerfactory.frontend.pages

import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import org.beerfactory.frontend.state.UserModel
import japgolly.scalajs.react.vdom.all._
import org.beerfactory.frontend.components.Commons._
import org.beerfactory.frontend.components.LoginForm
import org.beerfactory.frontend.utils.AjaxApiFacade
import org.beerfactory.shared.api.UserLoginRequest
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by njouanin on 07/11/16.
  */
object LoginPage {

  case class Props(router: RouterCtl[Page], proxy: ModelProxy[UserModel])

  def login(authData: String, password: String) = {
    Callback.future(
      AjaxApiFacade.login(UserLoginRequest(authData, password)).map(r ⇒ Callback.log(r.toString)))
  }

  private val component =
    ReactComponentB[Props]("LoginPage").render_P { props =>
      // format: off
      div(cls := "ui three column centered grid",
        GridRow(H1Header("Login to Beerfactory")),
        GridRow(LoginForm(LoginForm.Props(props.router, props.proxy, login)))
      )
      // format: on
    }.build

  def apply(router: RouterCtl[Page], proxy: ModelProxy[UserModel]) =
    component(Props(router, proxy))
}
