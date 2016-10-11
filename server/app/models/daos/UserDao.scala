/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package models.daos

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import models.daos.db.{DBLoginInfo, DBLoginInfoSchema, DBUser, DBUserSchema}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.inject.guice.GuiceInjectorBuilder
import slick.driver.JdbcProfile

/**
  * Created by nico on 12/06/2016.
  */
class UserDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends DBUserSchema with DBLoginInfoSchema with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

//  def insert(user: User) = {
//    db.run(users += user)
//  }

  def save(user: User) = {
    val user_pk = user.userId
    val dbLoginInfo = DBLoginInfo(user_pk, user.loginInfo.providerID, user.loginInfo.providerKey)
    val dbUser = DBUser(user_pk, user_pk, user.activated, user.email, user.firstName, user.lastName, user.fullName, user.locales)

    val actions = (for {
      _ ← dbUsers += dbUser
      _ ← dbLoginInfos += dbLoginInfo
    } yield()).transactionally
    db.run(actions)
  }

  def find(userId: String) = {
    val q = for {
      u ← dbUsers if u.id === userId
      l ← dbLoginInfos if l.id === userId
    } yield(u, l)
    db.run( q.result.headOption).map { resultOption ⇒
      resultOption match {
        case Some((dbUser, dbLoginInfo)) ⇒ User(
          dbUser.id,
          LoginInfo(dbLoginInfo.providerID, dbLoginInfo.providerKey),
          dbUser.activated,
          dbUser.email,
          dbUser.firstName,
          dbUser.lastName,
          dbUser.fullName,
          dbUser.locales
        )
      }
    }
  }

  def find(loginInfo: LoginInfo) = {
    val q = for {
      l ← dbLoginInfos.filter(dBLoginInfo => dBLoginInfo.providerID === loginInfo.providerID && dBLoginInfo.providerKey === loginInfo.providerKey)
      u ← dbUsers.filter(_.id === l.id)
    } yield u
  }

  def findByEmail(email: String) =
    db.run( dbUsers.filter(_.email.getOrElse("").toLowerCase === email.toLowerCase).result.headOption)
}
