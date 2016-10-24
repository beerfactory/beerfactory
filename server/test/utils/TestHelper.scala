package utils

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.PlaySpec
import play.api.inject.guice.GuiceApplicationBuilder

/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
class TestHelper extends PlaySpec with ScalaFutures with IntegrationPatience { self ⇒

  //Build add with in-memory database
  lazy val app = new GuiceApplicationBuilder().configure(Map(
    "slick.dbs.default.driver" -> "slick.driver.H2Driver$",
    "slick.dbs.default.db.driver" -> "org.h2.Driver",
    "slick.dbs.default.db.url" -> "jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=FALSE"
  )).build()
}
