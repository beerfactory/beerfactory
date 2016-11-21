/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.client.registration

import japgolly.scalajs.react.ReactDOM
import org.scalajs.dom
import slogging.{ConsoleLoggerFactory, LazyLogging, LoggerConfig}
import japgolly.scalajs.react.vdom.all._

import scala.scalajs.js.JSApp

object RegistrationMain extends JSApp with LazyLogging {
  def main(): Unit = {
    LoggerConfig.factory = ConsoleLoggerFactory()
    ReactDOM.render(div("Test registration"), dom.document.getElementById("root"))
  }
}
