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

case class UserCreateRequest(login: String, password: String, email: String) extends ServiceRequest
object UserCreateRequest {
  implicit val format: Format[UserCreateRequest] = Json.format[UserCreateRequest]
}
case class LoginRequest(emailOrLogin: String, password: String) extends ServiceRequest
object LoginRequest {
  implicit val format: Format[LoginRequest] = Json.format[LoginRequest]
}