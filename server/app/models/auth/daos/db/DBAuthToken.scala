package models.auth.daos.db

import java.sql.Timestamp
import java.time.{Instant, LocalDateTime, OffsetDateTime, ZonedDateTime}

import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

/**
  * Created by njouanin on 18/10/16.
  */


case class DBAuthToken(tokenId: String, userId: String, expiry: Instant)

trait DBAuthTokenSchema { self: HasDatabaseConfigProvider[JdbcProfile] ⇒
  import driver.api._

  class DBAuthTokenTable(tag: Tag) extends Table[DBAuthToken](tag, "auth_token") {
    def tokenId = column[String]("token_id", O.PrimaryKey)
    def userId = column[String]("user_fk")
    def expiry = column[Instant]("expiry")

    def * = (tokenId, userId, expiry) <> (DBAuthToken.tupled, DBAuthToken.unapply)
  }

  implicit val JavaLocalDateTimeMapper = MappedColumnType.base[Instant, Timestamp](
    instant => Timestamp.from(instant),
    ts => ts.toInstant
  )

  protected val DBAuthTokens = TableQuery[DBAuthTokenTable]
}
