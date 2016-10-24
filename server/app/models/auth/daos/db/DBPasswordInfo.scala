/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package models.auth.daos.db

import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

case class DBPasswordInfo(id: String, loginInfoFK: String, hasher: String, password: String, salt: Option[String] = None)

trait DBPasswordInfoSchema { self: HasDatabaseConfigProvider[JdbcProfile] ⇒
  import driver.api._

  class DBPasswordInfoTable(tag: Tag) extends Table[DBPasswordInfo](tag, "auth_password_info") {
    def id = column[String]("password_info_id", O.PrimaryKey)
    def loginInfoFK = column[String]("login_info_fk")
    def hasher = column[String]("hasher")
    def password = column[String]("password")
    def salt = column[Option[String]]("salt")

    def * = (id, loginInfoFK, hasher, password, salt) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
  }

  protected val DBPasswordInfos = TableQuery[DBPasswordInfoTable]
}