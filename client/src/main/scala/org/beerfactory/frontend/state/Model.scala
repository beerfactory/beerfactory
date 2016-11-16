/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.frontend.state

import diode.data.Pot
import diode.{Action, ActionHandler, Effect, ModelRW}
import org.beerfactory.frontend.utils.AjaxApiFacade
import org.beerfactory.shared.api.{ApiError, UserCurrentResponse, UserInfo}

// Application model
case class RootModel(userModel: UserModel, lastError: Option[ApiError])

case class UserModel(isAuthentified: Boolean = false,
                     userInfo: Option[UserInfo] = None,
                     authToken: Pot[String] = Pot.empty)

// Actions
case class UserLogin(token: String)        extends Action
case class SetUserInfo(userInfo: UserInfo) extends Action

case class SetError(error: ApiError) extends Action

class UserModelHandler[M](modelRW: ModelRW[M, UserModel]) extends ActionHandler(modelRW) {
  override def handle = {
    case UserLogin(token) =>
      AppCircuit.storeAuthToken(token)
      println("User login")
      updated(value.copy(isAuthentified = true, authToken = value.authToken.ready(token)),
              UserEffects.getUserInfo())
    case SetUserInfo(info) ⇒
      println(s"User info: $info")
      updated(value.copy(userInfo = Some(info)))
  }
}

//Effects
object UserEffects {
  import scala.concurrent.ExecutionContext.Implicits.global

  def getUserInfo() = {
    Effect(
      AjaxApiFacade.getCurrentUser.map(
        r ⇒
          r.fold(
            error ⇒ SetError(error),
            response ⇒ SetUserInfo(response.userInfo)
        )))
  }
}
