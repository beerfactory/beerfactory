/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.frontend.state

import diode.Circuit
import diode.data.{Empty, Ready}
import diode.react.ReactConnector
import org.scalajs.dom
import slogging.LazyLogging

/* Notes:
 * `connect` actively listens to changes in the model and then instructs React to update the component
 * `wrap` doesn't listen to changes. wrap provides a ModelProxy which is a convenience data structure to get access to things like dispatch
 */
object AppCircuit extends Circuit[RootModel] with ReactConnector[RootModel] with LazyLogging {

  private def getFromLocalStorage(key: String): String = {
    try {
      val item = dom.window.localStorage.getItem(key)
      logger.debug("localStorage.getItem({})={}", key, item)
      item
    } catch {
      case e: Exception ⇒
        logger.warn("localStorage.getItem has thrown a exception, returning empty item", e)
        ""
    }
  }

  def storeAuthToken(token: String) =
    dom.window.localStorage.setItem("beerfactory.auth.token", token)

  override protected def initialModel = {
    RootModel(userModel = UserModel(
                authToken = getFromLocalStorage("beerfactory.auth.token") match {
                  case null          ⇒ Empty
                  case token: String ⇒ Ready(token)
                }
              ),
              lastError = None)
  }

  override protected val actionHandler = composeHandlers(
    new UserModelHandler(zoomRW(_.userModel)((m, v) => m.copy(userModel = v)))
  )
}
