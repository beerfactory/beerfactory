/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.server.users

import java.time.OffsetDateTime
import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import org.beerfactory.server.models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.profile.RelationalProfile

import scala.concurrent.Future

/**
  * Created by nico on 12/06/2016.
  */
class UsersDao (driver: JdbcProfile) {
  import driver.api._

  val users = TableQuery[UserTable]

  class UserTable(tag: Tag) extends Table[User](tag, "USER") {
    def userId = column[String]("USERID", O.PrimaryKey)
    def login = column[String]("USERNAME")
    def password = column[String]("PASSWORD")
    def email = column[String]("EMAIL")
    def emailConfirmed = column[Boolean]("EMAIL_CONFIRMED", O.Default(false))
    def createdOn = column[OffsetDateTime]("CREATED_ON")
    def lastUpdatedOn = column[Option[OffsetDateTime]]("LAST_UPDATED_ON")
    def disabledOn = column[Option[OffsetDateTime]]("DISABLED_ON")
    def nickName = column[Option[String]]("NICKNAME")
    def firstName = column[Option[String]]("FIRSTNAME")
    def lastName = column[Option[String]]("LASTNAME")
    def locales = column[String]("LOCALES")
    def loginInfoProviderId = column[String]("LOGIN_INFO_PROVIDER_ID")
    def loginInfoProviderKey = column[String]("LOGIN_INFO_PROVIDER_KEY")
    def loginInfo = (loginInfoProviderId, loginInfoProviderKey) <> (LoginInfo.tupled, LoginInfo.unapply)

    def * = (userId, login, password, email, emailConfirmed, createdOn, lastUpdatedOn, disabledOn, nickName, firstName, lastName, locales) <> (User.tupled, User.unapply)
  }

  def insert(user: User) = {
    users += user
  }

  def findById(userId: String) = findOneWhere(_.userId === userId)

  def findByEmail(email: String) = findOneWhere(_.email.toLowerCase === email.toLowerCase)

  def findByLogin(login: String, caseSensitive:Boolean=true) = {
    caseSensitive match {
      case true => findOneWhere(_.login === login)
      case false => findOneWhere (_.login.toLowerCase === login.toLowerCase)
    }
  }

  def findOneWhere(condition: UserTable => Rep[Boolean]) = users.filter(condition).result.headOption
}