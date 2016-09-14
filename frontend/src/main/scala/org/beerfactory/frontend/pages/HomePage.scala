/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.frontend.pages

import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react.vdom.all._
import org.beerfactory.frontend.components.LoginView

object HomePage {

  val component = ReactComponentB.static("Home",
    LoginView()
  ).build
}
