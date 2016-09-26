/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend.users.api

import java.time.OffsetDateTime

import play.api.libs.json.{Format, Json}

sealed trait ServiceRequest

case class UserRegisterRequest(login: String,
                               password: String,
                               email: String,
                               nickName: Option[String],
                               firstName: Option[String],
                               lastName: Option[String],
                               locales: String) extends ServiceRequest
object UserRegisterRequest {
  implicit val format: Format[UserRegisterRequest] = Json.format[UserRegisterRequest]
}
case class LoginRequest(emailOrLogin: String, password: String) extends ServiceRequest
object LoginRequest {
  implicit val format: Format[LoginRequest] = Json.format[LoginRequest]
}