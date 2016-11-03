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
import japgolly.scalajs.react.extra.components.TriStateCheckbox.Props
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.all._
import org.beerfactory.frontend.GlobalStyles

import scalacss.ScalaCssReact._

object LoginView {
  private val loginView = ReactComponentB[Unit]("LoginView").render { _ =>
    div(GlobalStyles.loginView, div(LoginView.loginForm()), div("Test"))
  }.build

  val loginForm = ReactComponentB[Unit]("LoginForm").render { _ =>
    form(cls := "ui large form",
         div(cls := "ui stacked segment",
             div(cls := "field",
                 div(cls := "ui left icon input",
                     i(cls := "user icon"),
                     input(name := "username", placeholder := "Username"))),
             div(cls := "field",
                 div(cls := "ui left icon input",
                     i(cls := "lock icon"),
                     input(name := "password", placeholder := "Username"))),
             div(cls := "ui fluid large teal button", "Login")))
  }.build

  def apply(): ReactElement =
    loginView()
}
