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
import org.beerfactory.frontend.GlobalStyles
import org.beerfactory.frontend.pages.{Home, Page, Register}
import org.beerfactory.frontend.state.UserModel

object MainMenu {
  case class Props(router: RouterCtl[Page], currentLoc: Page, proxy: ModelProxy[UserModel])

  private class Backend($ : BackendScope[Props, Unit]) {
    def render(props: Props) = {
      def button(name: String, target: Page) =
        div(
          cls := "item",
          props.router.link(target)(name, cls := "ui basic button")
        )

      div(
        cls := "ui fixed menu",
        props.router.link(Home)(cls := "header item")(
          img(GlobalStyles.imgLogo, src := "/assets/images/logo.png"),
          "Beerfactory"
        ),
        if (props.proxy.value.userInfo.isReady) {
          div(cls := "right menu",
              div(cls := "item",
                  img(cls := "ui avatar image", src := props.proxy.value.userInfo.get.avatarUrl)))
        } else {
          div(
            cls := "right menu",
            button("Register", Register),
            button("Login", Home)
          )
        })
    }
  }

  private val component = ReactComponentB[Props]("MainMenu").renderBackend[Backend].build

  def apply(ctl: RouterCtl[Page], currentPage: Page, proxy: ModelProxy[UserModel]): ReactElement =
    component(Props(ctl, currentPage, proxy))
}
