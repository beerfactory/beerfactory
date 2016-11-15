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
import org.scalactic._
import Accumulation._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by njouanin on 07/11/16.
  */
object LoginPage {

  case class Props(router: RouterCtl[Page], proxy: ModelProxy[UserModel])

  case class State(authData: String, password: String, errors: Map[String, String])

  class Backend(scope: BackendScope[Props, State]) {

    def handleLogin(authData: String, password: String) = {
      Callback.future(
        AjaxApiFacade
          .login(UserLoginRequest(authData, password))
          .map(r ⇒ Callback.log(r.toString)))
    }

    def render(props: Props, state: State) =
      div(cls := "ui three column centered grid",
          GridRow(H1Header("Login to Beerfactory")),
          GridRow(LoginForm(LoginForm.Props(props.router, props.proxy, handleLogin))))
  }

  private val component =
    ReactComponentB[Props]("LoginPage")
      .initialState(State("", "", Map[String, String]()))
      .renderBackend[Backend]
      .build

  def apply(router: RouterCtl[Page], proxy: ModelProxy[UserModel]) =
    component(Props(router, proxy))
}
