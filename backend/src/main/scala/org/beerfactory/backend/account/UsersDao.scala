/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend.account

import java.time.OffsetDateTime
import java.util.UUID

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.StrictLogging
import org.beerfactory.backend.account.domain._
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

  def createUser(login: String,
                 passwordHash: String,
                 email: String,
                 createdOn: OffsetDateTime,
                 status: UserStatus): Future[User] = {
    ask(uuidActor, GetUUID).mapTo[UUID] flatMap {
      uuid =>
//        val newAccount = Account(uuid, login, passwordHash, email, createdOn, status)
//        db.run(accounts += newAccount).map(_ => newAccount)
        db.run(
          (accounts returning accounts.map(_.id) into ((account, _ ) => account))
            += User(uuid, login, passwordHash, email, createdOn, status)
        ).mapTo[User]
    }
  }

  def findById(userId: UUID): Future[Option[User]] = findOneWhere(_.id === userId)

  def findByEmail(email: String): Future[Option[User]] = findOneWhere(_.email.toLowerCase === email.toLowerCase)

  def findByLogin(login: String, caseSensitive:Boolean=true): Future[Option[User]] = {
    caseSensitive match {
      case true => findOneWhere(_.login === login)
      case false => findOneWhere (_.login.toLowerCase === login.toLowerCase)
    }
  }

  def findOneWhere(condition: UserTable => Rep[Boolean]) = db.run(accounts.filter(condition).result.headOption)
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
    case HsqldbEngine => ("ID", "USERNAME", "PASSWORD", "EMAIL", "CREATED_ON", "STATUS")
    case PostgresqlEngine => ("id", "username", "password", "email", "created_on", "status")
  }

  class UserTable(tag: Tag) extends Table[User](tag, tableName) {
    def id = column[UUID](colNames._1, O.PrimaryKey)
    def login = column[String](colNames._2)
    def passwordHash = column[String](colNames._3)
    def email = column[String](colNames._4)
    def createdOn = column[OffsetDateTime](colNames._5)
    def status = column[UserStatus](colNames._6)

    def * = (id, login, passwordHash, email, createdOn, status) <> (User.tupled, User.unapply)
  }

  val accounts = TableQuery[UserTable]


  implicit val userStatusColumnType = MappedColumnType.base[UserStatus, String](
    {
      status => status match {
        case NewAccount => "NewAccount"
        case ConfirmWait => "ConfirmWait"
        case Confirmed => "Confirmed"
        case Active => "Active"
        case Disabled => "Disabled"
      }
    },
    {
      str => str match {
        case "NewAccount" => NewAccount
        case "ConfirmWait" => ConfirmWait
        case "Confirmed" => Confirmed
        case "Active" => Active
        case "Disabled" => Disabled
      }
    }
  )}