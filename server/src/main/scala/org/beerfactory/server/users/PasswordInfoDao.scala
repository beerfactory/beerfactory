/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.server.users

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

class PasswordInfoDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends DelegableAuthInfoDAO[PasswordInfo] with UserSchema with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  implicit object LoginInfoShape extends CaseClassShape(LiftedLoginInfo.tupled, LoginInfo.tupled)

  def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] =
    db.run(users.filter(_.loginInfo === loginInfo))

  def add(loginInfo: LoginInfo, authInfo: T): Future[PasswordInfo]

  def update(loginInfo: LoginInfo, authInfo: T): Future[PasswordInfo]

  def save(loginInfo: LoginInfo, authInfo: T): Future[PasswordInfo]

  def remove(loginInfo: LoginInfo): Future[Unit]

}
