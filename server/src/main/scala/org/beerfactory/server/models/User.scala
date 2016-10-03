/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.server.models

import java.time.OffsetDateTime
import java.util.UUID

import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import com.mohiva.play.silhouette.impl.providers.{OAuth1Info, OAuth2Info}

case class Profile()

case class User(userId: UUID,
                loginInfo: LoginInfo,
                confirmed: Boolean,
                email: Option[String],
                firstName: Option[String],
                lastName: Option[String],
                fullName: Option[String],
                passwordInfo: Option[PasswordInfo],
                oauth1Info: Option[OAuth1Info],
                oAuth2Info: Option[OAuth2Info],
                avatarUrl: Option[String]) extends Identity
