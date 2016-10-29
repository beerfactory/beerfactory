/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package models.auth.daos

import java.time.Instant
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
  def findByUserName(userName: String): Future[Option[User]]
  def find(loginInfo: LoginInfo): Future[Option[User]]
}

class UserDaoImpl @Inject()(@Named("uuidActor") configuredActor: ActorRef,
                            protected val dbConfigProvider: DatabaseConfigProvider)
    extends UserDao
    with HasDatabaseConfigProvider[JdbcProfile]
    with DBUserSchema
    with DBLoginInfoSchema {

  import driver.api._

  implicit val timeout: Timeout = 5.seconds

  private def getRandomId(): Future[String] = {
    (configuredActor ? GetUUID).mapTo[String]
  }

  def save(user: User): Future[User] = {
    for {
      existingUser <- db.run(DBUsers.filter(_.id === user.userId).result.headOption)
      existingLoginInfoFK ← existingUser match {
        case Some(dbUser) => Future.successful(dbUser.loginInfoFK)
        case None         ⇒ getRandomId
      }
      user ← doSave(user, existingLoginInfoFK)
    } yield user
  }

  def doSave(user: User, loginInfoId: String): Future[User] = {
    val now = Instant.now()
    val dbUser = DBUser(id = user.userId,
                        loginInfoFK = loginInfoId,
                        emailVerified = user.emailVerified,
                        email = user.email,
                        userName = user.userName,
                        firstName = user.firstName,
                        lastName = user.lastName,
                        nickName = user.nickName,
                        avatarUrl = user.avatarUrl,
                        createdAt = user.createdAt match {
                          case None          ⇒ Some(now)
                          case Some(instant) ⇒ Some(instant)
                        },
                        updatedAt = Some(now),
                        deletedAt = user.deletedAt,
                        locale = user.locale)
    val dBLoginInfo =
      DBLoginInfo(loginInfoId, user.loginInfo.providerID, user.loginInfo.providerKey)
    val actions = (for {
      _ ← DBLoginInfos.insertOrUpdate(dBLoginInfo)
      _ ← DBUsers.insertOrUpdate(dbUser)
    } yield ()).transactionally
    db.run(actions).map(_ ⇒ mapToUser(Some((dbUser, dBLoginInfo))).get)
  }

  def find(userId: String): Future[Option[User]] = {
    val q = for {
      u ← DBUsers if u.id === userId
      l ← DBLoginInfos if l.id === u.loginInfoFK
    } yield (u, l)
    db.run(q.result.headOption).map(mapToUser)
  }

  override def findByUserName(userName: String): Future[Option[User]] = {
    val q = for {
      u ← DBUsers if u.userName === userName
      l ← DBLoginInfos if l.id === u.loginInfoFK
    } yield (u, l)
    db.run(q.result.headOption).map(mapToUser)
  }

  def find(loginInfo: LoginInfo): Future[Option[User]] = {
    val q = for {
      l ← DBLoginInfos.filter(dBLoginInfo =>
        dBLoginInfo.providerID === loginInfo.providerID && dBLoginInfo.providerKey === loginInfo.providerKey)
      u ← DBUsers.filter(_.loginInfoFK === l.id)
    } yield (u, l)
    db.run(q.result.headOption).map(mapToUser)
  }

  private def mapToUser(dbResult: Option[(DBUser, DBLoginInfo)]): Option[User] = {
    dbResult match {
      case Some((dbUser, dbLoginInfo)) ⇒
        Some(
          User(
            userId = dbUser.id,
            loginInfo = LoginInfo(dbLoginInfo.providerID, dbLoginInfo.providerKey),
            emailVerified = dbUser.emailVerified,
            email = dbUser.email,
            userName = dbUser.userName,
            firstName = dbUser.firstName,
            lastName = dbUser.lastName,
            nickName = dbUser.nickName,
            avatarUrl = dbUser.avatarUrl,
            locale = dbUser.locale,
            createdAt = dbUser.createdAt,
            updatedAt = dbUser.updatedAt,
            deletedAt = dbUser.deletedAt
          ))
      case _ ⇒ None
    }
  }

  def findByEmail(email: String) =
    db.run(DBUsers.filter(_.email.toLowerCase === email.toLowerCase).result.headOption)
}
