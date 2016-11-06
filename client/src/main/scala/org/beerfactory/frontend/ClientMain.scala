/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.frontend

import japgolly.scalajs.react
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.{
  BaseUrl,
  Redirect,
  Resolution,
  Router,
  RouterConfigDsl,
  RouterCtl
}
import japgolly.scalajs.react.vdom.all._
import org.beerfactory.frontend.components.{Footer, MainMenu}
import org.beerfactory.frontend.pages.{Home, HomePage, Login, Page}
import org.beerfactory.frontend.state.AppCircuit
import org.beerfactory.frontend.utils.AjaxApiFacade
import org.scalajs.dom
import scala.concurrent.ExecutionContext.Implicits.global

import scalacss.Defaults._
import scalacss.ScalaCssReact._
import scala.scalajs.js.JSApp

object ClientMain extends JSApp {

  val userWrapper = AppCircuit.connect(_.userModel)

  val userReader = AppCircuit.zoom(_.userModel)
  val userInfoWriter =
    AppCircuit.zoomRW(_.userModel.userInfo)((m, v) ⇒
      m.copy(userModel = m.userModel.copy(userInfo = v)))

  AjaxApiFacade.getCurrentUser.onSuccess {
    case Right(resp) ⇒ userInfoWriter.updated(Some(resp.userInfo))
    case Left(error) ⇒ println(error)
  }

  val routerConfig = RouterConfigDsl[Page].buildConfig { dsl =>
    import dsl._

    def isUserLoggedIn = userReader.zoom(_.isAuthentified).value

    val securedPages =
      (emptyRule
        | staticRoute(root, Home) ~> renderR(
          ctl => AppCircuit.wrap(_.userModel)(proxy => HomePage(ctl, proxy))))
        .addCondition(react.CallbackTo(isUserLoggedIn))(_ =>
          Some(redirectToPage(Login)(Redirect.Push)))

    (trimSlashes
      | securedPages).notFound(
      redirectToPage(if (isUserLoggedIn) Home else Login)(Redirect.Replace))
  }.renderWith(layout)

  def layout(c: RouterCtl[Page], r: Resolution[Page]) =
    div(
      cls := "ui vertical center aligned",
      userWrapper(proxy => MainMenu(c, r.page, proxy)),
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
