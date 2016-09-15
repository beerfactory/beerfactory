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

object AppCircuit extends Circuit[RootModel] with ReactConnector[RootModel] {

  override protected def initialModel = RootModel(UserModel(""))

  override protected val actionHandler = composeHandlers(
    new UserModelHandler(zoomRW(_.userModel)((m,v) => m.copy(userModel = v)))
  )
}
