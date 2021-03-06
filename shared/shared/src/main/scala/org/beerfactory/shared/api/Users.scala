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

sealed trait UsersApi

case class UserCreateRequest(email: String,
                             password: String,
                             userName: String,
                             firstName: Option[String] = None,
                             lastName: Option[String] = None,
                             nickName: Option[String] = None,
                             locale: Option[String] = None)
    extends UsersApi

case class UserCreateResponse(userInfo: UserInfo) extends UsersApi

case class UserCurrentResponse(userInfo: UserInfo) extends UsersApi

case class UserInfo(id: String,
                    createdAt: Option[Instant],
                    updatedAt: Option[Instant],
                    deletedAt: Option[Instant],
                    email: String,
                    emailVerified: Boolean,
                    userName: String,
                    firstName: Option[String],
                    lastName: Option[String],
                    nickName: Option[String],
                    locale: Option[String],
                    avatarUrl: Option[String],
                    authService: String,
                    authData: String) {
  def initials = {
    (firstName, lastName, nickName, userName) match {
      case (Some(u), Some(l), _, _) if !(u.isEmpty || l.isEmpty) ⇒
        (u.substring(0, 1) + l.substring(0, 1)).toUpperCase
      case (_, _, Some(n), _) if !n.isEmpty ⇒ n.substring(0, 1).toUpperCase()
      case (_, _, _, u) if !u.isEmpty       ⇒ u.substring(0, 1).toUpperCase
      case _                                ⇒ "?"
    }
  }
}

case class UserLoginRequest(authData: String, password: String) extends UsersApi
