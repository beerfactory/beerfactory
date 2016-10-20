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
                  loginInfoFK: String,
                  activated: Boolean,
                  email: Option[String],
                  firstName: Option[String],
                  lastName: Option[String],
                  fullName: Option[String],
                  avatarUrl: Option[String])

trait DBUserSchema { self: HasDatabaseConfigProvider[JdbcProfile] ⇒
  import driver.api._

  class DBUserTable(tag: Tag) extends Table[DBUser](tag, "user") {
    def id = column[String]("user_id", O.PrimaryKey)
    def loginInfoFK = column[String]("login_info_fk")
    def activated = column[Boolean]("activated", O.Default(false))
    def email = column[Option[String]]("email")
    def firstName = column[Option[String]]("firstname")
    def lastName = column[Option[String]]("lastname")
    def fullName = column[Option[String]]("fullname")
    def avatarUrl = column[Option[String]]("avatar_url")

    def * = (id, loginInfoFK, activated, email, firstName, lastName, fullName, avatarUrl) <> (DBUser.tupled, DBUser.unapply)
  }

  protected val DBUsers = TableQuery[DBUserTable]
}