/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.frontend.pages

import diode.data.Pot
import diode.react.{ModelProxy, ReactConnectProxy}
import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.all._
import org.beerfactory.frontend.Frontend.Page
import org.beerfactory.frontend.components.LoginView
import org.beerfactory.frontend.state.UserModel

object HomePage {

  case class Props(router: RouterCtl[Page], proxy: ModelProxy[Pot[UserModel]])

  case class State(userModelWrapper: ReactConnectProxy[Pot[UserModel]])

  private val component = ReactComponentB[Props]("Home")
    .initialState_P(props => State(props.proxy.connect(m => m)))
    .renderPS { (_, props, state) =>
      LoginView()
    }.build

  def apply(router: RouterCtl[Page], proxy: ModelProxy[Pot[UserModel]]) = component(Props(router, proxy))
}
