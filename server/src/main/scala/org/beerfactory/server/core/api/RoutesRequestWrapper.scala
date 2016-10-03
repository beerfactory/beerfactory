/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.server.core.api

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.CacheDirectives._
import akka.http.scaladsl.model.headers.{`Cache-Control`, `Last-Modified`, _}
import akka.http.scaladsl.server.{Directive1, ExceptionHandler, RejectionHandler}
import akka.http.scaladsl.server.Directives._
import com.typesafe.scalalogging.StrictLogging
import org.beerfactory.server.core.api.`X-Content-Type-Options`.`nosniff`
import org.beerfactory.server.core.api.`X-Frame-Options`.`DENY`
import org.beerfactory.server.core.api.`X-XSS-Protection`.`1; mode=block`

//from : https://github.com/softwaremill/bootzooka/blob/master/backend/src/main/scala/com/softwaremill/bootzooka/common/api/RoutesRequestWrapper.scala

trait RoutesRequestWrapper extends CacheSupport
  with SecuritySupport
  with StrictLogging {

  private val exceptionHandler = ExceptionHandler {
    case e: Exception =>
      logger.error(s"Exception during client request processing: ${e.getMessage}", e)
      _.complete(StatusCodes.InternalServerError, "Internal server error")
  }

  private val rejectionHandler = RejectionHandler.default
  private val logDuration = extractRequestContext.flatMap { ctx =>
    val start = System.currentTimeMillis()
    // handling rejections here so that we get proper status codes
    mapResponse { resp =>
      val d = System.currentTimeMillis() - start
      logger.info(s"[${resp.status.intValue()}] ${ctx.request.method.name} ${ctx.request.uri} took: ${d}ms")
      resp
    } & handleRejections(rejectionHandler)
  }

  val requestWrapper = logDuration &
    handleExceptions(exceptionHandler) &
    cacheImages &
    addSecurityHeaders &
    encodeResponse
}

trait CacheSupport {

  import akka.http.scaladsl.model.DateTime

  private val doNotCacheResponse = respondWithHeaders(
    `Last-Modified`(DateTime.now),
    `Expires`(DateTime.now),
    `Cache-Control`(`no-cache`, `no-store`, `must-revalidate`, `max-age`(0))
  )
  private val cacheSeconds = 60L * 60L * 24L * 30L
  private val cacheResponse = respondWithHeaders(
    `Expires`(DateTime(System.currentTimeMillis() + cacheSeconds * 1000L)),
    `Cache-Control`(`public`, `max-age`(cacheSeconds))
  )

  private def extensionTest(ext: String): Directive1[String] = pathSuffixTest((".*\\." + ext + "$").r)

  private def extensionsTest(exts: String*): Directive1[String] = exts.map(extensionTest).reduceLeft(_ | _)

  val cacheImages =
    extensionsTest("png", "svg", "gif", "woff", "jpg").flatMap { _ => cacheResponse } |
      doNotCacheResponse
}

trait SecuritySupport {
  val addSecurityHeaders = respondWithHeaders(
    `X-Frame-Options`(`DENY`),
    `X-Content-Type-Options`(`nosniff`),
    `X-XSS-Protection`(`1; mode=block`)
  )
}
