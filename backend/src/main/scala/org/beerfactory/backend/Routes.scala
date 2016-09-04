/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.Directives._
import org.beerfactory.backend.users.api.UsersRoutes
import org.beerfactory.backend.core.api.RoutesRequestWrapper
import org.beerfactory.backend.version.VersionRoutes
import org.beerfactory.backend.view.Index

trait Routes extends RoutesRequestWrapper
  with UsersRoutes
  with VersionRoutes {
  lazy val routes = requestWrapper {
    get {
      pathSingleSlash {
        complete(HttpResponse(entity=HttpEntity(ContentTypes.`text/html(UTF-8)`, Index.page.render)))
      }
    } ~
    getFromResourceDirectory("webapp") ~
    pathPrefix("api") {
      usersRoutes ~
        versionRoutes
    }
  }
}
