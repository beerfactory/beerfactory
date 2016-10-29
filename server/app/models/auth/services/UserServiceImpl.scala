/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package models.auth.services

import javax.inject.{Inject, Named}

import actors.UUIDActor.GetUUID
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import models.auth.User
import models.auth.daos.UserDao
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Handles actions to users.
  *
  * @param userDAO The user DAO implementation.
  */
class UserServiceImpl @Inject()(@Named("uuidActor") uuidActor: ActorRef, userDAO: UserDao)
    extends UserService {

  implicit val timeout = Timeout(5 seconds)

  /**
    * Retrieves a user that matches the specified login info.
    *
    * @param loginInfo The login info to retrieve a user.
    * @return The retrieved user or None if no user could be retrieved for the given login info.
    */
  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userDAO.find(loginInfo)

  def retrieve(userId: String): Future[Option[User]] = userDAO.find(userId)

  def retrieveByUserName(userName: String) = userDAO.findByUserName(userName)

  /**
    * Saves a user.
    *
    * @return The saved user.
    */
  def save(loginInfo: LoginInfo,
           emailVerified: Boolean = false,
           email: String,
           userName: Option[String],
           firstName: Option[String],
           lastName: Option[String],
           nickName: Option[String],
           locale: Option[String],
           avatarUrl: Option[String]): Future[User] = {
    for {
      uid ← ask(uuidActor, GetUUID).mapTo[String]
      dbUser ← userDAO.save(
        User(uid,
             loginInfo,
             emailVerified,
             email,
             userName,
             firstName,
             lastName,
             nickName,
             locale,
             avatarUrl))
    } yield dbUser

  }

  def save(user: User): Future[User] = userDAO.save(user)

  /**
    * Saves the social profile for a user.
    *
    * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
    *
    * @param profile The social profile to save.
    * @return The user for whom the profile was saved.
    */
  def save(profile: CommonSocialProfile): Future[User] = {
    userDAO.find(profile.loginInfo).flatMap {
      case Some(user) => // Update user with profile
        save(user.loginInfo,
             user.emailVerified,
             profile.email.getOrElse(""),
             None,
             profile.firstName,
             profile.lastName,
             profile.fullName,
             None,
             profile.avatarURL)
      case None => // Insert a new user
        save(profile.loginInfo,
             false,
             profile.email.getOrElse(""),
             None,
             profile.firstName,
             profile.lastName,
             profile.fullName,
             None,
             profile.avatarURL)
    }
  }
}
