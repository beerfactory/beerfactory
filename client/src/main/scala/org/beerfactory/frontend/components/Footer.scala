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
import org.beerfactory.frontend.GlobalStyles
import org.beerfactory.frontend.pages.Page
import scalacss.ScalaCssReact._

object Footer {
  // format: off
  private val component = ReactComponentB[RouterCtl[Page]]("Footer").render_P { ctl =>
    footer(GlobalStyles.footer,
      div(cls := "ui container",
        div(cls := "ui two column stackable flex grid",
          div(cls := "column",
            strong("Beerfactory"),
            a(href := "#", "Link"),
            a(href := "#", "Link"),
            a(href := "#", "Link"),
            a(href := "#", "Link")
          ),
          div(cls := "column right aligned",
            a(href := "#", "Link"),
            a(href := "#", "Link"),
            a(href := "#", "Link"),
            a(href := "#", "Link")
          )
        ),
        div(cls := "row",
          div(cls := "column",
            h1(cls := "ui centered header", "Login to Beerfactory")
          )
        )
      )
    )
  }.configure(Reusability.shouldComponentUpdate).build
  // format: on

  def apply(ctl: RouterCtl[Page]): ReactElement =
    component(ctl)
}
