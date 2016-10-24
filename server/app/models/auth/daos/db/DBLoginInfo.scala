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

case class DBLoginInfo(id: String, providerID: String, providerKey: String)

trait DBLoginInfoSchema { self: HasDatabaseConfigProvider[JdbcProfile] ⇒
  import driver.api._

  class DBLoginInfoTable(tag: Tag) extends Table[DBLoginInfo](tag, "auth_login_info") {
    def id = column[String]("login_info_id", O.PrimaryKey)
    def providerID = column[String]("provider_id")
    def providerKey = column[String]("provider_key")

    def * = (id, providerID, providerKey) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
  }

  protected val DBLoginInfos = TableQuery[DBLoginInfoTable]
}