/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend.users

import java.time.OffsetDateTime
import java.util.UUID

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.StrictLogging
import org.beerfactory.backend.users.domain._
import org.beerfactory.backend.core.UUIDActor.GetUUID
import org.beerfactory.backend.database.{HsqldbEngine, PostgresqlEngine, SqlDatabase}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by nico on 12/06/2016.
  */
class UsersDao(protected val database: SqlDatabase, uuidActor: ActorRef)(implicit val ec: ExecutionContext) extends UsersSchema with StrictLogging {
  import database._
  import database.driver.api._

  implicit val timeout = Timeout(5 seconds)

  /**
    * Create a user instance a insert it into the database
    * @param login
    * @param password
    * @param email
    * @param emailVerified
    * @param createdOn
    * @param lastUpdatedOn
    * @param disabledOn
    * @param nickName
    * @param firstName
    * @param lastName
    * @param locales
    * @return
    */
  def createUser(login: String,
                 password: String,
                 email: String,
                 emailVerified: Boolean,
                 createdOn: OffsetDateTime,
                 lastUpdatedOn: Option[OffsetDateTime],
                 disabledOn: Option[OffsetDateTime],
                 nickName: Option[String],
                 firstName: Option[String],
                 lastName: Option[String],
                 locales: String): Future[User] = {
    ask(uuidActor, GetUUID).mapTo[UUID] flatMap {
      uuid =>
        db.run(
          (users returning users.map(_.id) into ((account, _ ) => account))
            += User(uuid, login, password, email, emailVerified, createdOn, lastUpdatedOn, disabledOn, nickName, firstName, lastName, locales)
        ).mapTo[User]
    }
  }

  def createUser(login: String,
                 password: String,
                 email: String,
                 emailVerified: Boolean,
                 createdOn: OffsetDateTime,
                 nickName: Option[String],
                 firstName: Option[String],
                 lastName: Option[String],
                 locales: String): Future[User] = {
    createUser(login, password, email, emailVerified, createdOn, None, None, nickName, firstName, lastName, locales)
  }

  def findById(userId: UUID): Future[Option[User]] = findOneWhere(_.id === userId)

  def findByEmail(email: String): Future[Option[User]] = findOneWhere(_.email.toLowerCase === email.toLowerCase)

  def findByLogin(login: String, caseSensitive:Boolean=true): Future[Option[User]] = {
    caseSensitive match {
      case true => findOneWhere(_.login === login)
      case false => findOneWhere (_.login.toLowerCase === login.toLowerCase)
    }
  }

  def findOneWhere(condition: UserTable => Rep[Boolean]) = db.run(users.filter(condition).result.headOption)
}

/**
  * The schemas are in separate traits, so that if your DAO would require to access (e.g. join) multiple tables,
  * you can just mix in the necessary traits and have the `TableQuery` definitions available.
  */
trait UsersSchema {
  protected val database: SqlDatabase

  import database._
  import database.driver.api._

  private val tableName = database.driver.engine match {
    case HsqldbEngine => "USER"
    case PostgresqlEngine => "user"
  }

  private val colNames = database.driver.engine match {
    case HsqldbEngine => ("ID", "USERNAME", "PASSWORD", "EMAIL", "EMAIL_VERIFIED", "CREATED_ON", "LAST_UPDATED_ON", "DISABLED_ON", "NICKNAME", "FIRSTNAME", "LASTNAME", "LOCALES")
    case PostgresqlEngine => ("id", "username", "password", "email", "email_verified", "created_on", "last_updated_on", "disabled_on", "nickname", "firstname", "lastname", "locales")
  }

  class UserTable(tag: Tag) extends Table[User](tag, tableName) {
    def id = column[UUID](colNames._1, O.PrimaryKey)
    def login = column[String](colNames._2)
    def password = column[String](colNames._3)
    def email = column[String](colNames._4)
    def emailVerified = column[Boolean](colNames._5, O.Default(false))
    def createdOn = column[OffsetDateTime](colNames._6)
    def lastUpdatedOn = column[Option[OffsetDateTime]](colNames._7)
    def disabledOn = column[Option[OffsetDateTime]](colNames._8)
    def nickName = column[Option[String]](colNames._9)
    def firstName = column[Option[String]](colNames._10)
    def lastName = column[Option[String]](colNames._11)
    def locales = column[String](colNames._12)

    def * = (id, login, password, email, emailVerified, createdOn, lastUpdatedOn, disabledOn, nickName, firstName, lastName, locales) <> (User.tupled, User.unapply)
  }

  val users = TableQuery[UserTable]
}