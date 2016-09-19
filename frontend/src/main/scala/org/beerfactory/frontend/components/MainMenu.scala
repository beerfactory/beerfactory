/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.frontend.components

import diode.react.ModelProxy
import japgolly.scalajs.react.{BackendScope, ReactComponentB, ReactElement}
import japgolly.scalajs.react.extra.Reusability
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.all._

import scalacss.ScalaCssReact._
import org.beerfactory.frontend.Frontend.{Home, Page}
import org.beerfactory.frontend.GlobalStyles
import org.beerfactory.frontend.state.UserModel

object MainMenu {
  case class Props(router: RouterCtl[Page], currentLoc: Page, proxy: ModelProxy[UserModel])


  private class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      def button(name: String, target: Page) =
      div(
        cls:="item",
        props.router.link(target)(name, cls := "ui inverted basic blue button")
      )

      div(cls := "ui fixed inverted menu",
        props.router.link(Home)(cls:="header item") (
          img(GlobalStyles.imgLogo, src:="/resources/images/logo.png"),
          "Beerfactory"
        ),
        div(
          cls := "right menu",
          if(!props.proxy.value.isAuthentified) {
            button("Register", Home),
            button("Login", Home)
          }
        )
      )
    }
  }

  private val component = ReactComponentB[Props]("MainMenu")
    .renderBackend[Backend]
    .build

  def apply(ctl: RouterCtl[Page], currentPage: Page, proxy: ModelProxy[UserModel]): ReactElement =
    component(Props(ctl, currentPage, proxy))
}