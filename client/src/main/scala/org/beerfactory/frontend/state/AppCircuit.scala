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
import diode.data.Empty
import diode.react.ReactConnector
import org.beerfactory.frontend.DOMGlobalScope
import org.beerfactory.shared.api.UserLoginRequest
import org.scalajs.dom
import org.scalajs.dom.ext
import org.scalajs.dom.ext.Ajax
import upickle.default._

object AppCircuit extends Circuit[RootModel] with ReactConnector[RootModel] {

  override protected def initialModel = {
    val authToken = dom.window.localStorage.getItem("authToken")
    Ajax.post(url = "/api/v1/users/login",
              data = write(UserLoginRequest("test", "password")),
              headers = Map("Content-Type" → "application/json"))
    RootModel(UserModel(locale = "fr"))
  } //DOMGlobalScope.acceptLang()))

  override protected val actionHandler = composeHandlers(
    new UserModelHandler(zoomRW(_.userModel)((m, v) => m.copy(userModel = v)))
  )
}
