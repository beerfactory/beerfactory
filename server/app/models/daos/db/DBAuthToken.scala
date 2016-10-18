package models.daos.db

import java.time.ZonedDateTime

import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

/**
  * Created by njouanin on 18/10/16.
  */


case class DBAuthToken(tokenId: String, userId: String, expiry: ZonedDateTime)

trait DBAuthTokenSchema { self: HasDatabaseConfigProvider[JdbcProfile] ⇒
  import driver.api._

  class DBUserTable(tag: Tag) extends Table[DBAuthToken](tag, "auth_token") {
    def tokenId = column[String]("token_id", O.PrimaryKey)
    def userFK = column[String]("user_fk")
    def expiry = column[ZonedDateTime]("expiry")

    def * = (tokenId, userFK, expiry) <> (DBAuthToken.tupled, DBAuthToken.unapply)
  }

  protected val DBAuthTokens = TableQuery[DBAuthToken]
}
