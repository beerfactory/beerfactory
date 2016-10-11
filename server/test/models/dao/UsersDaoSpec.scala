/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package models.dao

import java.time.{OffsetDateTime, ZoneId}

import akka.actor.ActorSystem
import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import models.daos.UserDao
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import utils.TestConfiguration

class UsersDaoSpec extends PlaySpec with OneAppPerSuite with TestConfiguration {

  val usersDao = app.injector.instanceOf[UserDao]

  "UserDao" must {
    "save new user" in {
      val newUser = User("ID", LoginInfo("testProvider", "testKey"), true, Some("email@test.com"), Some("firstName"), Some("LastName"), Some("fullName"), "fr")
      usersDao.save(newUser)
    }
  }
}