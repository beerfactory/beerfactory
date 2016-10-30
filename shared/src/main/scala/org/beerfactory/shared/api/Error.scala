/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.shared.api

import play.api.libs.json.{Format, JsString, JsValue, Json}

case class Error(id: String, detail: Option[JsValue], statusCode: Int)

object Error {
  implicit val errorFormat: Format[Error] = Json.format[Error]

  def apply(id: String, detail: String, statusCode: Int) =
    new Error(id, Some(JsString(detail)), statusCode)

  def apply(id: String, statusCode: Int) =
    new Error(id, None, statusCode)
}
