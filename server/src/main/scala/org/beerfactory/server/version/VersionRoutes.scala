/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.server.version

import akka.http.scaladsl.server.Directives._
import play.api.libs.json.{Format, Json}
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._

trait VersionRoutes {

  val versionRoutes = path("version") {
    complete {
      VersionJson(Version.name, Version.version)
    }
  }
}

case class VersionJson(title: String, version: String)
object VersionJson {
  implicit val format: Format[VersionJson] = Json.format[VersionJson]
}
