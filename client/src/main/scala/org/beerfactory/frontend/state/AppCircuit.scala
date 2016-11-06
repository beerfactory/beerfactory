/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.frontend.state

import diode.{ActionHandler, Circuit}
import diode.data.{Empty, Pot, Ready}
import diode.react.ReactConnector
import org.beerfactory.shared.api.{UserInfo, UserLoginRequest}
import org.scalajs.dom
import org.scalajs.dom.ext
import org.scalajs.dom.ext.Ajax

object AppCircuit extends Circuit[RootModel] with ReactConnector[RootModel] {

  private val authTokenStorageKey = "beerfactory.auth.token"

  private def getFromLocalStorage(key: String): String = {
    try {
      dom.window.localStorage.getItem(authTokenStorageKey)
    } catch {
      case _: Exception ⇒ ""
    }
  }

  override protected def initialModel = {
    RootModel(
      userModel = UserModel(
        authToken = getFromLocalStorage(authTokenStorageKey) match {
          case null          ⇒ Empty
          case token: String ⇒ Ready(token)
        }
      ))
  }

  override protected val actionHandler = composeHandlers(
    new UserModelHandler(zoomRW(_.userModel)((m, v) => m.copy(userModel = v)))
  )
}
