/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package models.auth.daos

import javax.inject.{Inject, Named}

import actors.UUIDActor.GetUUID
import akka.actor.ActorRef
import akka.util.Timeout
import com.mohiva.play.silhouette.api.LoginInfo
import models.auth.daos.db.{DBLoginInfo, DBLoginInfoSchema, DBUser, DBUserSchema}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import akka.pattern.ask
import models.auth.User

/**
  * Created by nico on 12/06/2016.
  */

trait UserDao {
  def save(user: User): Future[User]
  def find(userId: String): Future[Option[User]]
  def find(loginInfo: LoginInfo): Future[Option[User]]
}

class UserDaoImpl @Inject()( @Named("uuidActor") configuredActor: ActorRef,
                             protected val dbConfigProvider: DatabaseConfigProvider)
  extends UserDao with HasDatabaseConfigProvider[JdbcProfile] with DBUserSchema with DBLoginInfoSchema {

  import driver.api._

  implicit val timeout: Timeout = 5.seconds

  private def getRandomId(): Future[String] = {
    (configuredActor ? GetUUID).mapTo[String]
  }

  def save(user: User) = {
    db.run(DBUsers.filter(_.id === user.userId).result.headOption).flatMap {
      case Some(dbUser) => Future.successful(dbUser.loginInfoFK)
      case None ⇒ getRandomId
    }.flatMap { loginInfoPK ⇒
      val actions = (for {
        _ ← DBLoginInfos.insertOrUpdate(DBLoginInfo(loginInfoPK, user.loginInfo.providerID, user.loginInfo.providerKey))
        _ ← DBUsers.insertOrUpdate(DBUser(user.userId, loginInfoPK, user.activated, user.email, user.firstName, user.lastName, user.fullName, user.avatarUrl))
      } yield()).transactionally
      db.run(actions)
    }.map { _ ⇒ user }
  }

  def find(userId: String): Future[Option[User]] = {
    val q = for {
      u ← DBUsers if u.id === userId
      l ← DBLoginInfos if l.id === u.loginInfoFK
    } yield(u, l)
    db.run( q.result.headOption).map(mapToUser)
  }

  def find(loginInfo: LoginInfo): Future[Option[User]] = {
    val q = for {
      l ← DBLoginInfos.filter(dBLoginInfo => dBLoginInfo.providerID === loginInfo.providerID && dBLoginInfo.providerKey === loginInfo.providerKey)
      u ← DBUsers.filter(_.loginInfoFK === l.id)
    } yield (u, l)
    db.run( q.result.headOption).map(mapToUser)
  }

  private def mapToUser(dbResult: Option[(DBUser, DBLoginInfo)]): Option[User] = {
    dbResult match {
      case Some((dbUser, dbLoginInfo)) ⇒ Some(User(
        dbUser.id,
        LoginInfo(dbLoginInfo.providerID, dbLoginInfo.providerKey),
        dbUser.activated,
        dbUser.email,
        dbUser.firstName,
        dbUser.lastName,
        dbUser.fullName,
        dbUser.avatarUrl
      ))
      case _ ⇒ None
    }
  }

  def findByEmail(email: String) =
    db.run( DBUsers.filter(_.email.getOrElse("").toLowerCase === email.toLowerCase).result.headOption)
}
