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

case class DBLoginInfo(id: String, providerID: String, providerKey: String)

trait DBLoginInfoSchema { self: HasDatabaseConfigProvider[JdbcProfile] ⇒
  import driver.api._

  class DBLoginInfoTable(tag: Tag) extends Table[DBLoginInfo](tag, "LOGIN_INFO") {
    def id = column[String]("ID", O.PrimaryKey)
    def providerID = column[String]("PROVIDER_ID")
    def providerKey = column[String]("PROVIDER_KEY")

    def * = (id, providerID, providerKey) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
  }

  val dbLoginInfos = TableQuery[DBLoginInfoTable]
}