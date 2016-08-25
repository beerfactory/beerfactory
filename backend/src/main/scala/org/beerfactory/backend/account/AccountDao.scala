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
class AccountDao(protected val database: SqlDatabase, uuidActor: ActorRef)(implicit val ec: ExecutionContext) extends AccountSchema with StrictLogging {
  import database._
  import database.driver.api._

  implicit val timeout = Timeout(5 seconds)

  def createAccount(login: String,
                    passwordHash: String,
                    email: String,
                    createdOn: OffsetDateTime,
                    status: AccountStatus): Future[Account] = {
    ask(uuidActor, GetUUID).mapTo[UUID] flatMap {
      uuid =>
//        val newAccount = Account(uuid, login, passwordHash, email, createdOn, status)
//        db.run(accounts += newAccount).map(_ => newAccount)
        db.run(
          (accounts returning accounts.map(_.id) into ((account, _ ) => account))
            += Account(uuid, login, passwordHash, email, createdOn, status)
        ).mapTo[Account]
    }
  }

  def findById(accountId: UUID): Future[Option[Account]] = findOneWhere(_.id === accountId)

  def findByEmail(email: String): Future[Option[Account]] = findOneWhere(_.email.toLowerCase === email.toLowerCase)

  def findByLogin(login: String, caseSensitive:Boolean=true): Future[Option[Account]] = {
    caseSensitive match {
      case true => findOneWhere(_.login === login)
      case false => findOneWhere(_.login.toLowerCase === login.toLowerCase())
    }
  }

  def findOneWhere(condition: AccountTable => Rep[Boolean]) = db.run(accounts.filter(condition).result.headOption)
}

/**
  * The schemas are in separate traits, so that if your DAO would require to access (e.g. join) multiple tables,
  * you can just mix in the necessary traits and have the `TableQuery` definitions available.
  */
trait AccountSchema {
  protected val database: SqlDatabase

  import database._
  import database.driver.api._

  private val tableName = database.engine match {
    case HsqldbEngine => "ACCOUNTS"
    case PostgresqlEngine => "accounts"
  }

  private val colNames = database.engine match {
    case HsqldbEngine => ("ACCOUNT_ID", "LOGIN", "PASSWORD_HASH", "EMAIL", "CREATED_ON", "STATUS")
    case PostgresqlEngine => ("account_id", "login", "password_hash", "email", "created_on", "status")
  }

  class AccountTable(tag: Tag) extends Table[Account](tag, tableName) {
    def id = column[UUID](colNames._1, O.PrimaryKey)
    def login = column[String](colNames._2)
    def passwordHash = column[String](colNames._3)
    def email = column[String](colNames._4)
    def createdOn = column[OffsetDateTime](colNames._5)
    def status = column[AccountStatus](colNames._6)

    def * = (id, login, passwordHash, email, createdOn, status) <> (Account.tupled, Account.unapply)
  }

  val accounts = TableQuery[AccountTable]


  implicit val accountStatusColumnType = MappedColumnType.base[AccountStatus, String](
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