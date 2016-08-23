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

import akka.actor.ActorSystem
import org.beerfactory.backend.account.domain.{Account, NewAccount}
import org.beerfactory.backend.core.UUIDActor
import org.beerfactory.backend.test.FlatSpecWithDb
import org.scalatest.Matchers

class AccountDaoSpec extends FlatSpecWithDb with Matchers {
  behavior of "AccountDao"

  lazy val actorSystem = ActorSystem()
  import scala.concurrent.ExecutionContext.Implicits.global

  val uuidActor = actorSystem.actorOf(UUIDActor.props(), name = "uuidActor")
  val accountDao = new AccountDao(sqlDatabase, uuidActor)

  it should "add new account" in {
    val now = OffsetDateTime.now()
    val newAccount = accountDao.createAccount(
      "login", "passwordHash", "toto@toto.com", now, NewAccount).futureValue
    newAccount shouldBe a [Account]
    newAccount.login shouldEqual "login"
    newAccount.passwordHash shouldEqual "passwordHash"
    newAccount.email shouldEqual "toto@toto.com"
    newAccount.createdOn shouldEqual now
    newAccount.status shouldEqual NewAccount
  }

  override protected def afterAll() {
    actorSystem.terminate().futureValue
    super.afterAll()
  }

  it should "find an existing account by its Id" in {
    val someAccount = accountDao.createAccount(
      "login", "passwordHash", "toto@toto.com", OffsetDateTime.now(), NewAccount).futureValue
    val testAccount = accountDao.findById(someAccount.id).futureValue
    testAccount shouldEqual Some(someAccount)
  }

  it should "find an existing account by its email" in {
    val someAccount = accountDao.createAccount(
      "login", "passwordHash", "toto@toto.com", OffsetDateTime.now(), NewAccount).futureValue
    val testAccount = accountDao.findByEmail(someAccount.email).futureValue
    testAccount shouldEqual Some(someAccount)
  }
}