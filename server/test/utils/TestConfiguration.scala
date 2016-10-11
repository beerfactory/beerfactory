package utils

import com.typesafe.config.ConfigFactory
import org.scalatestplus.play.OneAppPerSuite
import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder

/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
trait TestConfiguration { self: OneAppPerSuite ⇒
  implicit override lazy val app = new GuiceApplicationBuilder()
    .configure(Configuration(ConfigFactory.load("test.conf")))
    .build()
}
