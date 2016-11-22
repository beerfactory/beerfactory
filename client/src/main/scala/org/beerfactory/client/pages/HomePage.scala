/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.client.pages

import diode.react.{ModelProxy, ReactConnectProxy}
import japgolly.scalajs.react.{BackendScope, ReactComponentB}
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.all._
import org.beerfactory.client.state.UserModel
import org.beerfactory.shared.components.Commons._

object HomePage {

  case class Props(router: RouterCtl[Page], proxy: ModelProxy[UserModel])

  case class State()

  class Backend(scope: BackendScope[Props, State]) {

    def render(props: Props, state: State) =
      div(cls := "ui three column centered grid", GridRow(H1Header("Home page")))
  }

  private val component =
    ReactComponentB[Props]("LoginPage").initialState(State()).renderBackend[Backend].build

  def apply(router: RouterCtl[Page], proxy: ModelProxy[UserModel]) =
    component(Props(router, proxy))
}
