/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend.users.api

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import com.typesafe.scalalogging.StrictLogging
import org.beerfactory.backend.users.UsersService
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import org.beerfactory.backend.ServerConfig
import org.beerfactory.backend.core.http.Directives._
import org.beerfactory.backend.version.Version
import pdi.jwt.algorithms.JwtHmacAlgorithm
import pdi.jwt.{JwtAlgorithm, JwtClaim}

trait UsersRoutes extends StrictLogging {
  def usersService: UsersService
  def config: ServerConfig

  val jwtAlgorithm = JwtAlgorithm.allHmac().find(_.name.toLowerCase == config.jwtAlgorithmName.toLowerCase) match {
    case Some(x:JwtHmacAlgorithm) => x
    case None => {
      logger.warn(s"Incorrect or unknown JWT signing algorithm '$config.jwtAlgorithmName' specified in configuration. Using 'HmacSHA256' default")
      JwtAlgorithm.HS256
    }
  }
  val jwtTTL = config.jwtTTL.getSeconds
  val jwtSecretKey = config.jwtSecretKey

  private def initAuthToken(tokenId: String, subject: String, claimContent:String):JwtClaim = {
    JwtClaim(claimContent).
      withId(tokenId).
      by(Version.prettyName).
      about(subject).
      issuedNow.
      expiresIn(jwtTTL)
  }
  val usersRoutes = pathPrefix("account") {
    path("register") {
      post {
        entity(as[UserRegisterRequest]) { accountRegistration =>
          onSuccess(usersService.registerUser(accountRegistration)) {
            case failure:RegistrationFailure => complete(StatusCodes.BadRequest, failure)
            case RegistrationSuccess => complete("success")
          }
        }
      }
    } ~
      path("authenticate") {
        post {
          entity(as[LoginRequest]) { request =>
            onSuccess(usersService.authenticate(request)) {
              case failure:AuthenticateFailure => complete(StatusCodes.BadRequest, failure)
              case success:AuthenticationSuccess =>
                setAuthToken(
                  initAuthToken(success.tokenId, success.userId.toString, s"""{"uid": "${success.userId}"}"""),
                  jwtAlgorithm,
                  jwtSecretKey) {
                  complete("success")
                }
            }
          }
        }
      }
  }
}
