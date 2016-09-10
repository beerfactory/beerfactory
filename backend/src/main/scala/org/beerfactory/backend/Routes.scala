/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import com.typesafe.scalalogging.StrictLogging
import org.beerfactory.backend.users.api.UsersRoutes
import org.beerfactory.backend.core.api.RoutesRequestWrapper
import org.beerfactory.backend.version.VersionRoutes
import org.beerfactory.backend.view.Index
import org.webjars.WebJarAssetLocator

import scala.util.{Failure, Success, Try}

trait Routes extends StrictLogging
    with RoutesRequestWrapper with UsersRoutes with VersionRoutes {

  val webJarLocator = new WebJarAssetLocator()

  lazy val routes = requestWrapper {
    get {
      pathSingleSlash {
       complete(HttpResponse(entity=HttpEntity(ContentTypes.`text/html(UTF-8)`, Index.page.render)))
      } ~
      path("assets" / Segment / Remaining) { (webJar, partialPath) =>
        Try(webJarLocator.getFullPath(webJar, partialPath)) match {
          case Success(path) => getFromResource(path)
          case Failure(e) => {
            logger.warn(s"file '$partialPath' and/or webjar '$webJar' not found")
            logger.debug(s"$e")
            complete(StatusCodes.NotFound)
          }
        }
      } ~
      pathPrefix("resources") {
        getFromResourceDirectory("")
      } ~
      getFromResourceDirectory("")
    } ~
    pathPrefix("api") {
      usersRoutes ~
        versionRoutes
    }
  }
}
