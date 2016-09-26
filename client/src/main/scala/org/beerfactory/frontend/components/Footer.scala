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
import org.beerfactory.frontend.Frontend.Page
import org.beerfactory.frontend.GlobalStyles

object Footer {
  private val component = ReactComponentB[RouterCtl[Page]]("MainMenu")
    .render_P { ctl =>
      div(cls := "ui inverted vertical segment",
        div(cls:= "ui right aligned segment",
          "Version xxx"
        )
      )
    }
    .configure(Reusability.shouldComponentUpdate)
    .build

  def apply(ctl: RouterCtl[Page]): ReactElement =
    component(ctl)
}
