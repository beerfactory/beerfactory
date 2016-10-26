/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package controllers.auth.api

import play.api.libs.json.{Format, Json}

case class SignUp( email: String,
                   password: String,
                   firstName: Option[String],
                   lastName: Option[String])
object SignUp {
  implicit val format: Format[SignUp] = Json.format[SignUp]
}
