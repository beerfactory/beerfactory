/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package models.auth

import java.time.Instant

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}

case class User(userId: String,
                loginInfo: LoginInfo,
                emailVerified: Boolean,
                email: String,
                userName: String,
                firstName: Option[String] = None,
                lastName: Option[String] = None,
                nickName: Option[String] = None,
                locale: Option[String] = None,
                avatarUrl: Option[String] = None,
                createdAt: Option[Instant] = None,
                updatedAt: Option[Instant] = None,
                deletedAt: Option[Instant] = None)
    extends Identity
