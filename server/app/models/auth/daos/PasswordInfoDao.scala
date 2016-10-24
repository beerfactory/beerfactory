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

import scala.concurrent.duration._
import akka.pattern.ask
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.{AuthInfoDAO, DelegableAuthInfoDAO}
import models.auth.daos.db.{DBLoginInfoSchema, DBPasswordInfo, DBPasswordInfoSchema}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import play.api.libs.concurrent.Execution.Implicits._


import scala.concurrent.Future

class PasswordInfoDao @Inject()( @Named("uuidActor") configuredActor: ActorRef,
                                 protected val dbConfigProvider: DatabaseConfigProvider)
  extends DelegableAuthInfoDAO[PasswordInfo] with HasDatabaseConfigProvider[JdbcProfile] with DBPasswordInfoSchema with DBLoginInfoSchema {

  import driver.api._

  implicit val timeout: Timeout = 5.seconds

  private def getRandomId(): Future[String] = {
    (configuredActor ? GetUUID).mapTo[String]
  }

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
    val actions = for {
      l ← DBLoginInfos.filter(row => row.providerID === loginInfo.providerID && row.providerKey === loginInfo.providerKey)
      passwordInfo ← DBPasswordInfos if passwordInfo.loginInfoFK === l.id
    } yield passwordInfo
    db.run( actions.result.headOption ).map {
      case Some(result) ⇒ Some(PasswordInfo(result.hasher, result.password, result.salt))
      case _ ⇒ None
    }
  }

  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = save(loginInfo, authInfo)

  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = save(loginInfo, authInfo)

  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    val actions = for {
      authInfoId ← getRandomId()
      loginInfoId ← db.run(
        DBLoginInfos.filter(row => row.providerID === loginInfo.providerID && row.providerKey === loginInfo.providerKey)
          .map(_.id).result.head
      )
    } yield(authInfoId, loginInfoId)

    actions.flatMap {
      case (authInfoId, loginInfoId) ⇒
        db.run(DBPasswordInfos.insertOrUpdate(
          DBPasswordInfo(authInfoId, loginInfoId, authInfo.hasher, authInfo.password, authInfo.salt)
        ))
    }.map { _ ⇒ authInfo}
  }

  override def remove(loginInfo: LoginInfo): Future[Unit] = {
    db.run(
      DBLoginInfos.filter(row => row.providerID === loginInfo.providerID && row.providerKey === loginInfo.providerKey)
      .map(_.id).result.headOption
    ).flatMap { loginInfoId ⇒
      db.run(DBPasswordInfos.filter(passwordInfo => passwordInfo.loginInfoFK === loginInfoId).delete)
    }.map (_ ⇒ ())
  }
}
