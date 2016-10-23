/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package models

import java.time.OffsetDateTime
import java.util.UUID

import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import com.mohiva.play.silhouette.impl.providers.{OAuth1Info, OAuth2Info}

case class User(userId: String,
                loginInfo: LoginInfo,
                activated: Boolean,
                email: Option[String],
                firstName: Option[String],
                lastName: Option[String],
                fullName: Option[String],
                avatarUrl: Option[String]) extends Identity
{
  /**
    * Tries to construct a name.
    *
    * @return Maybe a name.
    */
  def name = fullName.orElse {
    firstName -> lastName match {
      case (Some(f), Some(l)) => Some(f + " " + l)
      case (Some(f), None) => Some(f)
      case (None, Some(l)) => Some(l)
      case _ => None
    }
  }
}
