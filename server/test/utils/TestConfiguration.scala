package utils

import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers, Suite}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.OneAppPerSuite
import play.api.{Configuration, Play}
import play.api.inject.guice.GuiceApplicationBuilder

/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
trait TestConfiguration extends BeforeAndAfterAll with ScalaFutures with IntegrationPatience { this: Suite =>

  implicit val app = new GuiceApplicationBuilder()
    .configure(Configuration(ConfigFactory.load("test.conf")))
    .build()

  override protected def beforeAll() {
    super.beforeAll()
  }

  override protected def afterAll() {
    super.afterAll()
//    Play.stop(app)
  }

}
