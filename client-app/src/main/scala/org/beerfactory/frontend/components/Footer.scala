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
import org.beerfactory.frontend.pages.Page

import scalacss.ScalaCssReact._
import scalacss.Defaults._

object Footer {

  object Styles extends StyleSheet.Inline {
    import dsl._

    val footer = style(
      height(7 em),
      backgroundColor(c"#353535"),
      position.relative,
      paddingTop(40 px),
      color(c"#fff")
    )

    val footerLink = style(
      color(c"#a0a0a0"),
      paddingRight(10 px),
      paddingLeft(10 px),
      &.hover(color(c"#a0a0a0"))
    )
  }
  Styles.addToDocument()

  private val component = ReactComponentB[RouterCtl[Page]]("Footer").render_P { ctl =>
    footer(Styles.footer,
           div(cls := "ui container",
               div(cls := "ui two column stackable flex grid",
                   div(cls := "column",
                       strong("Beerfactory"),
                       a(Styles.footerLink, href := "#", "Link"),
                       a(Styles.footerLink, href := "#", "Link"),
                       a(Styles.footerLink, href := "#", "Link"),
                       a(Styles.footerLink, href := "#", "Link")),
                   div(cls := "column right aligned",
                       a(Styles.footerLink, href := "#", "Link"),
                       a(Styles.footerLink, href := "#", "Link"),
                       a(Styles.footerLink, href := "#", "Link"),
                       a(Styles.footerLink, href := "#", "Link")))))
  }.configure(Reusability.shouldComponentUpdate).build

  def apply(ctl: RouterCtl[Page]): ReactElement =
    component(ctl)
}
