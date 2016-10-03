/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.server

import java.util.Locale.LanguageRange

import akka.http.scaladsl.model.headers.Language
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import com.typesafe.scalalogging.StrictLogging
import org.beerfactory.server.core.api.RoutesRequestWrapper
import org.beerfactory.server.version.VersionRoutes
import org.beerfactory.server.view.Index
import org.webjars.WebJarAssetLocator

import scala.util.{Failure, Success, Try}

trait Routes extends StrictLogging
    with RoutesRequestWrapper with UsersRoutes with VersionRoutes {

  def config: ServerConfig

  lazy val alternateLanguages = config.alternateLanguages.replaceAll("\\s+","").split(",").map(Language(_))

  val webJarLocator = new WebJarAssetLocator()

  lazy val routes = requestWrapper {
    get {
      pathSingleSlash {
        selectPreferredLanguage(config.mainLanguage, alternateLanguages: _*) { lang =>
          complete(HttpResponse(entity=HttpEntity(ContentTypes.`text/html(UTF-8)`, Index(lang.toString).render)))
        }
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