/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.shared.api

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class UserCreateRequest(email: String,
                             password: String,
                             userName: Option[String],
                             firstName: Option[String],
                             lastName: Option[String],
                             nickName: Option[String],
                             locale: Option[String])

object UserCreateRequest {
  val userCreateRequestReads: Reads[UserCreateRequest] = (
    (__ \ "email").read[String](email) and
      (__ \ "password").read[String] and
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

case class UserCreateResponse()
