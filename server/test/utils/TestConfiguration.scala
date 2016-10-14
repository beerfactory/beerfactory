package utils

import com.typesafe.config.ConfigFactory
import models.daos.UserDao
import org.scalatest._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.OneAppPerSuite
import play.api.db.Database
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

  //val database = Databases.inMemory()
  implicit val app = new GuiceApplicationBuilder(configuration = Configuration(ConfigFactory.load("conf/application.conf")))
    //.overrides(bind[Database].toInstance(database))
    //.configure(Configuration(ConfigFactory.load("test.conf")))
    //.in(Mode.Dev)
    .build()

  override protected def beforeAll() {
    super.beforeAll()
  }

  override protected def afterAll() {
    super.afterAll()
//    Play.stop(app)
  }

  override protected def beforeEach() {
    super.beforeEach()
    println("beforeEach")
    //Evolutions.applyEvolutions(app.injector.instanceOf[Database])
  }

  override protected def afterEach() {
    println("afterEach")
    //Evolutions.cleanupEvolutions(database)
    super.afterEach()
  }
}
