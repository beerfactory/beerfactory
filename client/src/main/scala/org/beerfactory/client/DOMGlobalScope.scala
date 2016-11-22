/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.client

import org.scalajs.dom.raw.HTMLDocument

import scala.scalajs.js

@js.native
object DOMGlobalScope extends js.GlobalScope {
  val document: HTMLDocument = js.native

  /**
    * This Javascript method is added by the server on applcation load and return the user preferred language
    * (ie : as set by HTTP-ACCEPT-LANGUAGE header)
    * @return user preferred language code
    */
  def acceptLang(): String = js.native
}
