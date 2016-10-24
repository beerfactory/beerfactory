/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package models.dao

import com.mohiva.play.silhouette.api.LoginInfo
import models.auth.User
import models.auth.daos.UserDao
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play._
import utils.TestHelper
import views.html.app

class UsersDaoSpec extends TestHelper {

  "UserDao" must {
    "save new user" in {
      val usersDao = app.injector.instanceOf[UserDao]
      val newUser = User("ID", LoginInfo("testProvider", "testKey"), true, Some("email@test.com"), Some("firstName"), Some("LastName"), Some("fullName"), None)
      val u = usersDao.save(newUser).futureValue
      u mustBe newUser
      val ret = usersDao.find("ID").futureValue
      ret mustBe Some(newUser)
    }
  }
}