package utils

import com.typesafe.config.ConfigFactory
import models.auth.daos.UserDao
import org.scalatest._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.OneAppPerSuite
import play.api.db.{DBApi, Database}
import play.api.db.evolutions.Evolutions
import play.api.{Configuration, Mode, Play}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers
import play.api.inject.bind
/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
trait TestConfiguration extends BeforeAndAfterAll with BeforeAndAfterEach with ScalaFutures with IntegrationPatience { this: Suite =>

  val app = new GuiceApplicationBuilder().build()
  val dbapi = app.injector.instanceOf[DBApi]

  override protected def beforeEach() {
    super.beforeEach()
    //Evolutions.applyEvolutions(dbapi.database("default"))
  }

  override protected def afterEach() {
    //Evolutions.cleanupEvolutions(dbapi.database("default"))
    super.afterEach()
  }
}
