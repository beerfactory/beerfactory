/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.frontend.components

import japgolly.scalajs.react.{ReactComponentB, ReactElement}
import japgolly.scalajs.react.extra.Reusability
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.all._
import scalacss.ScalaCssReact._
import org.beerfactory.frontend.Frontend.{Home, Page}
import org.beerfactory.frontend.GlobalStyles

object MainMenu {
  private val component = ReactComponentB[RouterCtl[Page]]("MainMenu")
    .render_P { ctl =>
      def button(name: String, target: Page) =
        div(
          cls:="item",
          ctl.link(target)(name, cls := "ui inverted basic blue button")
        )

      div(cls := "ui fixed inverted menu",
        ctl.link(Home)(cls:="header item") (
          img(GlobalStyles.imgLogo, src:="/resources/images/logo.png"),
          "Beerfactory"
        ),
        div(
          cls := "right menu",
          button("Register", Home),
          button("Login", Home)
        )
      )
    }
    .configure(Reusability.shouldComponentUpdate)
    .build

  def apply(ctl: RouterCtl[Page]): ReactElement =
    component(ctl)
}