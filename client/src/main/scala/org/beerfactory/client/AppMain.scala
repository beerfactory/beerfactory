/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.client

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
import org.beerfactory.client.components.{Footer, MainMenu}
import org.beerfactory.client.pages._
import org.beerfactory.client.state.{AppCircuit, UserLogin}
import org.beerfactory.client.components.MainMenu
import org.scalajs.dom
import slogging.{ConsoleLoggerFactory, LazyLogging, LoggerConfig}

import scala.concurrent.ExecutionContext.Implicits.global
import scalacss.Defaults._
import scalacss.ScalaCssReact._
import scala.scalajs.js.JSApp

object AppMain extends JSApp with LazyLogging {

  private def getFromLocalStorage(key: String): String = {
    try {
      val item = dom.window.localStorage.getItem(key)
      logger.trace("localStorage.getItem({})={}", key, item)
      item
    } catch {
      case e: Exception ⇒
        logger.warn("localStorage.getItem has thrown a exception, returning empty item", e)
        ""
    }
  }

  val userConnection = AppCircuit.connect(_.userModel)

  val routerConfig = RouterConfigDsl[Page].buildConfig { dsl =>
    import dsl._

    def isUserLoggedIn = AppCircuit.zoom(_.userModel.authToken.isReady).value

    val securedPages =
      (emptyRule
        | staticRoute(root, Home) ~> renderR(
          ctl => AppCircuit.wrap(_.userModel)(proxy => HomePage(ctl, proxy))))
        .addCondition(react.CallbackTo(isUserLoggedIn))(_ =>
          Some(redirectToPage(Login)(Redirect.Push)))

    (trimSlashes
      | staticRoute("login", Login) ~> renderR(
        ctl => AppCircuit.wrap(_.userModel)(proxy => LoginPage(ctl, proxy)))
      | staticRoute("register", Register) ~> renderR(ctl => RegisterPage(ctl))
      | securedPages)
      .notFound(redirectToPage(if (isUserLoggedIn) Home else Login)(Redirect.Replace))
  }.renderWith(layout)

  def layout(controller: RouterCtl[Page], r: Resolution[Page]) =
    div(
      cls := "ui vertical center aligned",
      userConnection(proxy => MainMenu(controller, r.page, proxy)),
      div(GlobalStyles.mainContainer, r.render()),
      Footer(controller)
    )

  def main(): Unit = {
    LoggerConfig.factory = ConsoleLoggerFactory()
    GlobalStyles.addToDocument()

    // Try to see if user is already authenticated (by an existing token in localStorage)
    getFromLocalStorage("beerfactory.auth.token") match {
      case token: String ⇒ AppCircuit.dispatch(UserLogin(token))
      case _             ⇒ ()
    }

    val router = Router(BaseUrl.fromWindowOrigin_/, routerConfig)
    // tell React to render the router in the document body
    ReactDOM.render(router(), dom.document.getElementById("root"))
  }
}
