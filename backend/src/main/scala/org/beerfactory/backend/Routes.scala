/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend

import akka.http.scaladsl.server.Directives._
import org.beerfactory.backend.account.api.AccountRoutes
import org.beerfactory.backend.core.api.RoutesRequestWrapper
import org.beerfactory.backend.version.VersionRoutes

trait Routes extends RoutesRequestWrapper
  with AccountRoutes
  with VersionRoutes {
  lazy val routes = requestWrapper {
    pathPrefix("api") {
      accountRoutes ~
        versionRoutes
    } ~
      getFromResourceDirectory("webapp") ~
      path("") {
        getFromResource("webapp/index.html")
      }
  }
}
