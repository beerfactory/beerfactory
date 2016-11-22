package org.beerfactory.client.pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.all._
import org.beerfactory.client.GlobalStyles
import org.beerfactory.shared.components.Commons._
import org.beerfactory.client.components.RegisterForm
import org.beerfactory.client.utils.AjaxApiFacade
import org.beerfactory.shared.api.{UserCreateRequest, UserInfo}
import org.scalajs.dom
import slogging.LazyLogging
import scalacss.ScalaCssReact._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by njouanin on 18/11/16.
  */
object RegisterPage {
  case class Props(router: RouterCtl[Page])

  case class State(registered: Boolean, userInfo: Option[UserInfo], errorMessage: Option[String])

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
                  s"Registration failed with error code ${apiError.statusCode} (${apiError.id})"
              }
              scope.modState(s ⇒ s.copy(errorMessage = Some(message)))
            case Right(response) ⇒
              logger.debug(s"response")
              scope.modState(s => s.copy(registered = true, userInfo = Some(response.userInfo)))
          }
      }
    }

    def render(props: Props, state: State) =
      div(
        cls := "ui grid",
        GridCenteredRow(H1Header("Register to Beerfactory")),
        if (state.registered) {
          val email = state.userInfo match {
            case Some(info) => info.email
            case None       => "NO_ADDRESS"
          }
          GridCenteredRow(
            div(GlobalStyles.leftAlignedSuccessMessageWithIcon,
                i(cls := "check-square-o icon"),
                div(cls := "content",
                    div(cls := "header", "Registration completed (NOT_YET_IMPLEMENTED)"),
                    p("A confirmation email has been sent to ",
                      b(email),
                      ". Please read it before ",
                      props.router.link(Home)("logging in"),
                      "."),
                    p("Didn't received a confirmation email? ",
                      props.router.link(Home)("Request a new one")))))
        } else
          GridRow(RegisterForm(RegisterForm.Props(props.router, handleSubmit, state.errorMessage)))
      )
  }

  private val component =
    ReactComponentB[Props]("RegisterPage")
      .initialState(State(false, None, None))
      .renderBackend[Backend]
      .build

  def apply(router: RouterCtl[Page]) =
    component(Props(router))
}
