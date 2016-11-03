/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package models.dao

import java.time.Instant

import com.mohiva.play.silhouette.api.LoginInfo
import models.auth.User
import models.auth.daos.UserDao
import utils.TestHelper

class UsersDaoSpec extends TestHelper {

  "UserDao" must {
    "save new user" in {
      val usersDao = app.injector.instanceOf[UserDao]
      val newUser = User("ID",
                         LoginInfo("testProvider", "testKey"),
                         true,
                         "email@test.com",
                         Some("userName"),
                         Some("firstName"),
                         Some("LastName"),
                         Some("nickName"))
      val u = usersDao.save(newUser).futureValue
      u.createdAt.get mustBe a[Instant]
      u.updatedAt.get mustBe a[Instant]
      u.deletedAt mustBe None
    }
  }
}
