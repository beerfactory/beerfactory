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
import org.beerfactory.frontend.Frontend.{Home, Page}

object MainMenu {
  private val component = ReactComponentB[RouterCtl[Page]]("MainMenu")
    .render_P { ctl =>
      def button(name: String, target: Page) =
        div(
          cls:="item",
          ctl.link(target)(name, cls := "ui inverted button")
        )

      div(
        cls := "ui inverted menu",
        div(
          cls := "header item",
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