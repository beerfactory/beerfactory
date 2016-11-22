/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.client.state

import diode.data.{Pot, Ready}
import diode.{Action, ActionHandler, Effect, ModelRW}
import org.beerfactory.client.utils.AjaxApiFacade
import org.beerfactory.shared.api.{ApiError, UserCurrentResponse, UserInfo}
import slogging.LazyLogging

// Application model
case class RootModel(userModel: UserModel, lastError: Option[ApiError])

case class UserModel(userInfo: Pot[UserInfo], authToken: Pot[String])

// Actions
case class UserLogin(token: String)        extends Action
case class SetUserInfo(userInfo: UserInfo) extends Action

case class SetError(error: ApiError) extends Action

class UserModelHandler[M](modelRW: ModelRW[M, UserModel])
    extends ActionHandler(modelRW)
    with LazyLogging {
  override def handle = {
    case UserLogin(token) =>
      logger.trace("Handling UserLogin action. Token={}", token)
      AppCircuit.storeAuthToken(token)
      updated(value.copy(authToken = value.authToken.ready(token)), UserEffects.getUserInfo())
    case SetUserInfo(info) ⇒
      logger.trace("Handling SetUserInfo action. info={}", info)
      updated(value.copy(userInfo = Ready(info)))
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
