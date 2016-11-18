/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package models.auth.daos.db

import java.sql.Timestamp
import java.time.Instant

import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

case class DBUser(id: String,
                  loginInfoFK: String,
                  emailVerified: Boolean,
                  email: String,
                  userName: String,
                  firstName: Option[String],
                  lastName: Option[String],
                  nickName: Option[String],
                  locale: Option[String],
                  avatarUrl: Option[String],
                  createdAt: Option[Instant],
                  updatedAt: Option[Instant],
                  deletedAt: Option[Instant])

trait DBUserSchema { self: HasDatabaseConfigProvider[JdbcProfile] ⇒
  import driver.api._

  class DBUserTable(tag: Tag) extends Table[DBUser](tag, "auth_user") {
    def id            = column[String]("user_id", O.PrimaryKey)
    def loginInfoFK   = column[String]("login_info_fk")
    def emailVerified = column[Boolean]("email_verified", O.Default(false))
    def email         = column[String]("email")
    def userName      = column[String]("username")
    def firstName     = column[Option[String]]("firstname")
    def lastName      = column[Option[String]]("lastname")
    def nickName      = column[Option[String]]("nickname")
    def locale        = column[Option[String]]("locale")
    def avatarUrl     = column[Option[String]]("avatar_url")
    def createdAt     = column[Option[Instant]]("created_at")
    def updatedAt     = column[Option[Instant]]("updated_at")
    def deletedAt     = column[Option[Instant]]("deleted_at")

    def * =
      (id,
       loginInfoFK,
       emailVerified,
       email,
       userName,
       firstName,
       lastName,
       nickName,
       locale,
       avatarUrl,
       createdAt,
       updatedAt,
       deletedAt) <> (DBUser.tupled, DBUser.unapply)
  }

  protected val DBUsers = TableQuery[DBUserTable]

  implicit val JavaLocalDateTimeMapper = MappedColumnType.base[Instant, Timestamp](
    instant => Timestamp.from(instant),
    ts => ts.toInstant
  )
}
