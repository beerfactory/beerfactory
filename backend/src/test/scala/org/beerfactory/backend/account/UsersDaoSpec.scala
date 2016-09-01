/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend.account

import java.time.{OffsetDateTime, ZoneId}

import akka.actor.ActorSystem
import org.beerfactory.backend.account.domain.{User, NewAccount}
import org.beerfactory.backend.core.UUIDActor
import org.beerfactory.backend.test.FlatSpecWithDb
import org.scalatest.{BeforeAndAfterAll, Matchers}

class UsersDaoSpec extends FlatSpecWithDb with Matchers with BeforeAndAfterAll {
  behavior of "UsersDao"

  lazy val actorSystem = ActorSystem()
  import scala.concurrent.ExecutionContext.Implicits.global

  val uuidActor = actorSystem.actorOf(UUIDActor.props(), name = "uuidActor")
  val usersDao = new UsersDao(sqlDatabase, uuidActor)

  override def afterAll() {
    actorSystem.terminate().futureValue
    super.afterAll
  }

  it should "add new user" in {
    val now = OffsetDateTime.now()
    val newUser = usersDao.createUser(
      "username", "passwordHash", "toto@toto.com", now, NewAccount).futureValue
    newUser shouldBe a [User]
    newUser.login shouldEqual "username"
    newUser.passwordHash shouldEqual "passwordHash"
    newUser.email shouldEqual "toto@toto.com"
    newUser.createdOn shouldEqual now
    newUser.status shouldEqual NewAccount
  }

  it should "find an existing account by its Id" in {
    val usersDao = new UsersDao(sqlDatabase, uuidActor)
    val someUser = usersDao.createUser(
      "login", "passwordHash", "toto@toto.com", OffsetDateTime.now(ZoneId.of("UTC")), NewAccount).futureValue
    val testUser = usersDao.findById(someUser.id).futureValue
    testUser shouldEqual Some(someUser)
  }

  it should "find an existing account by its email" in {
    val usersDao = new UsersDao(sqlDatabase, uuidActor)
    val someUser = usersDao.createUser(
      "login", "passwordHash", "titi@toto.com", OffsetDateTime.now(ZoneId.of("UTC")), NewAccount).futureValue
    val testUser = usersDao.findByEmail(someUser.email).futureValue
    testUser shouldEqual Some(someUser)
  }
}