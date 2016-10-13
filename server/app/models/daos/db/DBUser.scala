/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package models.daos.db

import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

case class DBUser(id: String,
                  loginInfoId: String,
                  activated: Boolean,
                  email: Option[String],
                  firstName: Option[String],
                  lastName: Option[String],
                  fullName: Option[String],
                  locales: String)

trait DBUserSchema { self: HasDatabaseConfigProvider[JdbcProfile] ⇒
  import driver.api._

  class DBUserTable(tag: Tag) extends Table[DBUser](tag, "users_nnn") {
    def id = column[String]("ID", O.PrimaryKey)
    def loginInfoId = column[String]("LOGIN_INFO_FK")
    def activated = column[Boolean]("ACTIVATED", O.Default(false))
    def email = column[Option[String]]("EMAIL")
    def firstName = column[Option[String]]("FIRSTNAME")
    def lastName = column[Option[String]]("LASTNAME")
    def fullName = column[Option[String]]("FULLNAME")
    def locales = column[String]("LOCALES")

    def * = (id, loginInfoId, activated, email, firstName, lastName, fullName, locales) <> (DBUser.tupled, DBUser.unapply)
  }

  val dbUsers = TableQuery[DBUserTable]
}