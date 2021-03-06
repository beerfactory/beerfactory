package org.beerfactory.client.pages

import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import org.beerfactory.client.state.{UserLogin, UserModel}
import japgolly.scalajs.react.vdom.all._
import org.beerfactory.shared.components.Commons._
import org.beerfactory.client.utils.AjaxApiFacade
import org.beerfactory.shared.api.UserLoginRequest
import org.scalactic._
import Accumulation._
import org.beerfactory.client.components.LoginForm

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by njouanin on 07/11/16.
  */
object LoginPage {

  case class Props(router: RouterCtl[Page], proxy: ModelProxy[UserModel])

  case class State(authData: String, password: String, errorMessage: Option[String])

  class Backend(scope: BackendScope[Props, State]) {

    def handleLogin(authData: String, password: String) = {
      Callback.future(
        AjaxApiFacade.login(UserLoginRequest(authData, password))
          map {
            case Left(apiError) ⇒
              val message = apiError.id match {
                case "user.login.invalid.credentials" ⇒
                  "Invalid e-mail/username or password"
                case _ ⇒
                  s"Login failed with error code ${apiError.statusCode} (${apiError.id})"
              }
              scope.modState(s ⇒ s.copy(errorMessage = Some(message)))
            case Right(token) ⇒
              scope.props.flatMap(props =>
                props.proxy.dispatchCB(UserLogin(token)) >> props.router.set(Home))
          })
    }

    def render(props: Props, state: State) =
      div(
        cls := "ui three column centered grid",
        GridRow(H1Header("Login to Beerfactory")),
        GridRow(
          LoginForm(LoginForm.Props(props.router, props.proxy, handleLogin, state.errorMessage))))
  }

  private val component =
    ReactComponentB[Props]("LoginPage")
      .initialState(State("", "", None))
      .renderBackend[Backend]
      .build

  def apply(router: RouterCtl[Page], proxy: ModelProxy[UserModel]) =
    component(Props(router, proxy))
}
