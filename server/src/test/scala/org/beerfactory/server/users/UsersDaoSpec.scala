/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.server.users

import java.time.{OffsetDateTime, ZoneId}

import akka.actor.ActorSystem
import org.beerfactory.server.core.UUIDActor
import org.beerfactory.server.models.User
import org.beerfactory.server.test.FlatSpecWithDb
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
    val newUser = usersDao.createUser("username", "passwordHash", "toto@toto.com", false, now, Some(now), Some(now),
      Some("nickName"), Some("firstName"), Some("lastName"),"locales").futureValue
    newUser shouldBe a [User]
    newUser.login shouldEqual "username"
    newUser.password shouldEqual "passwordHash"
    newUser.email shouldEqual "toto@toto.com"
    newUser.emailVerified shouldEqual false
    newUser.createdOn shouldEqual now
    newUser.lastUpdatedOn shouldEqual Some(now)
    newUser.disabledOn shouldEqual Some(now)
    newUser.nickName shouldEqual Some("nickName")
    newUser.firstName shouldEqual Some("firstName")
    newUser.lastName shouldEqual Some("lastName")
    newUser.locales shouldEqual "locales"
  }

  it should "find an existing account by its Id" in {
    val now = OffsetDateTime.now(ZoneId.of("UTC"))
    val usersDao = new UsersDao(sqlDatabase, uuidActor)
    val someUser = usersDao.createUser("username", "passwordHash", "toto@toto.com", false, now, Some(now), Some(now),
      Some("nickName"), Some("firstName"), Some("lastName"),"locales").futureValue
    val testUser = usersDao.findById(someUser.id).futureValue
    testUser shouldEqual Some(someUser)
  }

  it should "find an existing account by its email" in {
    val now = OffsetDateTime.now(ZoneId.of("UTC"))
    val usersDao = new UsersDao(sqlDatabase, uuidActor)
    val someUser = usersDao.createUser("username", "passwordHash", "user@domain.com", false, now, Some(now), Some(now),
      Some("nickName"), Some("firstName"), Some("lastName"),"locales").futureValue
    val testUser = usersDao.findByEmail(someUser.email).futureValue
    testUser shouldEqual Some(someUser)
  }
}