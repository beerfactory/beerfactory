/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.frontend

import japgolly.scalajs.react.ReactDOM
import japgolly.scalajs.react.extra.router.{BaseUrl, Redirect, Resolution, Router, RouterConfigDsl, RouterCtl}
import japgolly.scalajs.react.vdom.all._
import org.beerfactory.frontend.components.{Footer, MainMenu}
import org.beerfactory.frontend.pages.HomePage
import org.beerfactory.frontend.state.AppCircuit
import org.scalajs.dom

import scalacss.Defaults._
import scalacss.ScalaCssReact._
import scala.scalajs.js.JSApp

object Frontend extends JSApp {
  sealed trait Page
  case object Home extends Page

  val routerConfig = RouterConfigDsl[Page].buildConfig { dsl =>
    import dsl._

    ( trimSlashes
      | staticRoute(root, Home) ~> renderR(ctl => AppCircuit.wrap(_.userModel)(proxy => HomePage.component(ctl, proxy)))
      .notFound(redirectToPage(Home)(Redirect.Replace))
      .renderWith(layout)
  }

  def layout(c: RouterCtl[Page], r: Resolution[Page]) =
    div(
      cls := "ui vertical center aligned",
      MainMenu(c),
      div(GlobalStyles.mainContainer, r.render()),
      Footer(c)
    )

  def main(): Unit = {
    GlobalStyles.addToDocument()

    val router = Router(BaseUrl.until_#, routerConfig)
    // tell React to render the router in the document body
    ReactDOM.render(router(), dom.document.getElementById("root"))
  }
}
