/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.shared.api

import java.time.Instant

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class UserCreateRequest(email: String,
                             password: String,
                             userName: Option[String] = None,
                             firstName: Option[String] = None,
                             lastName: Option[String] = None,
                             nickName: Option[String] = None,
                             locale: Option[String] = None)

object UserCreateRequest {
  val userCreateRequestReads: Reads[UserCreateRequest] = (
    (__ \ "email").read[String](email) and
      (__ \ "password")
        .read[String](minLength[String](1)) and //avoid empty passwords. Real validation rules should go in controller
      (__ \ "userName").readNullable[String] and
      (__ \ "firstName").readNullable[String] and
      (__ \ "lastName").readNullable[String] and
      (__ \ "nickName").readNullable[String] and
      (__ \ "locale").readNullable[String]
  )(UserCreateRequest.apply _)

  val userCreateRequestWrites: Writes[UserCreateRequest] = (
    (__ \ "email").write[String] and
      (__ \ "password").write[String] and
      (__ \ "userName").writeNullable[String] and
      (__ \ "firstName").writeNullable[String] and
      (__ \ "lastName").writeNullable[String] and
      (__ \ "nickName").writeNullable[String] and
      (__ \ "locale").writeNullable[String]
  )(unlift(UserCreateRequest.unapply))

  implicit val format: Format[UserCreateRequest] =
    Format(userCreateRequestReads, userCreateRequestWrites)
}

case class UserCreateResponse(id: String,
                              createdAt: Option[Instant],
                              updatedAt: Option[Instant],
                              deletedAt: Option[Instant],
                              email: String,
                              emailVerified: Boolean,
                              userName: Option[String],
                              firstName: Option[String],
                              lastName: Option[String],
                              nickName: Option[String],
                              locale: Option[String],
                              avatarUrl: Option[String],
                              authService: String,
                              authData: String)

object UserCreateResponse {
  implicit val format: Format[UserCreateResponse] = Json.format[UserCreateResponse]
}

case class UserLoginRequest(authData: String, password: String)

object UserLoginRequest {
  implicit val format: Format[UserLoginRequest] = Json.format[UserLoginRequest]
}
