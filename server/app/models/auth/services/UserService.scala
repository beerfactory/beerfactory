/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package models.auth.services

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import models.auth.User

import scala.concurrent.Future

trait UserService extends IdentityService[User] {

  def retrieve(userId: String): Future[Option[User]]

  def retrieveByUserName(userName: String): Future[Option[User]]

  /**
    * Saves a user.
    *
    * @return The saved user.
    */
  def save(loginInfo: LoginInfo,
           emailVerified: Boolean = false,
           email: String,
           userName: String,
           firstName: Option[String],
           lastName: Option[String],
           nickName: Option[String],
           locale: Option[String],
           avatarUrl: Option[String]): Future[User]

  def save(user: User): Future[User]

  /**
    * Saves the social profile for a user.
    *
    * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
    *
    * @param profile The social profile to save.
    * @return The user for whom the profile was saved.
    */
  def save(profile: CommonSocialProfile): Future[User]
}
