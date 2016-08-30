/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend.account.api

import play.api.libs.json.{Format, Json}

sealed trait ServiceRequest

case class AccountRegisterRequest(login: String, password: String, email: String) extends ServiceRequest
object AccountRegisterRequest {
  implicit val format: Format[AccountRegisterRequest] = Json.format[AccountRegisterRequest]
}
case class AuthenticateRequest(emailOrLogin: String, password: String) extends ServiceRequest
object AuthenticateRequest {
  implicit val format: Format[AuthenticateRequest] = Json.format[AuthenticateRequest]
}