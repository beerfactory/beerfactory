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
import diode.{Action, ActionHandler, ModelRW}
import org.beerfactory.shared.api.UserInfo

// Application model
case class RootModel(userModel: UserModel)

case class UserModel(isAuthentified: Boolean = false,
                     userInfo: Option[UserInfo] = None,
                     authToken: Pot[String] = Pot.empty)

// Actions
case class SetAuthToken(token: String) extends Action

class UserModelHandler[M](modelRW: ModelRW[M, UserModel]) extends ActionHandler(modelRW) {
  override def handle = {
    case SetAuthToken(token) => updated(value.copy(authToken = value.authToken.ready(token)))
  }
}
