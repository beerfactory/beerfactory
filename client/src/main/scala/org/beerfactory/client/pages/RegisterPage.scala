package org.beerfactory.client.pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.all._
import org.beerfactory.shared.components.Commons._
import org.beerfactory.client.components.RegisterForm
import org.beerfactory.client.utils.AjaxApiFacade
import org.beerfactory.shared.api.UserCreateRequest
import org.scalajs.dom
import slogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by njouanin on 18/11/16.
  */
object RegisterPage {
  case class Props(router: RouterCtl[Page])

  case class State(errorMessage: Option[String])

  class Backend(scope: BackendScope[Props, State]) extends LazyLogging {

    def handleSubmit(formData: RegisterForm.FormData) = {
      Callback.future {
        AjaxApiFacade
          .register(
            UserCreateRequest(formData.email,
                              formData.password,
                              formData.userName,
                              formData.firstName,
                              formData.lastName,
                              None,
                              Some(dom.window.navigator.language)))
          .map {
            case Left(apiError) ⇒
              val message = apiError.id match {
                case "create.user.alreadyExist" ⇒
                  "Some user already exists with this username or email address"
                case _ ⇒
                  s"Registration failed with error code ${apiError.statusCode} ($apiError.id)"
              }
              scope.modState(s ⇒ s.copy(errorMessage = Some(message)))
            case Right(response) ⇒
              logger.debug(s"response")
              Callback.empty
          }
      }
    }

    def render(props: Props, state: State) =
      div(
        cls := "ui grid",
        GridCenteredRow(H1Header("Register to Beerfactory")),
        GridRow(RegisterForm(RegisterForm.Props(props.router, handleSubmit, state.errorMessage))))
  }

  private val component =
    ReactComponentB[Props]("RegisterPage").initialState(State(None)).renderBackend[Backend].build

  def apply(router: RouterCtl[Page]) =
    component(Props(router))
}
