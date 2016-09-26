/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend.core.http

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive0, Directive1}
import akka.http.scaladsl.server.Directives._
import pdi.jwt.algorithms.JwtHmacAlgorithm
import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtJson}

import scala.util.{Failure, Success}

trait Directives {
  val httpResponseHeader: String = "Set-Authorization"
  val httpRequestTokenHeader: String = "Authorization"

  def setAuthToken(claim: JwtClaim, algo:JwtHmacAlgorithm, key: String): Directive0 = {
    respondWithHeader(RawHeader(httpResponseHeader, JwtJson.encode(claim, key, algo)))
  }

  def validateAuthToken(algo:JwtHmacAlgorithm, key: String): Directive1[JwtClaim] = {
    optionalHeaderValueByName(httpRequestTokenHeader).flatMap {
      case None => reject(AuthorizationFailedRejection)
      case Some(token) =>
        JwtJson.decode(token, key, Seq(algo)) match {
          case Success(claim: JwtClaim) => provide(claim)
          case Failure(e) => reject(AuthorizationFailedRejection)
        }
    }
  }
}

object Directives extends Directives