/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package controllers.api.auth

import play.api.libs.json.{Format, Json}

case class RegisterRequest(email: String,
                           password: String,
                           firstName: Option[String],
                           lastName: Option[String])

object RegisterRequest {
  implicit val format: Format[RegisterRequest] = Json.format[RegisterRequest]
}
