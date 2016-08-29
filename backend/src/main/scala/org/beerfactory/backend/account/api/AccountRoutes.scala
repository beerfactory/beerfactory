/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend.account.api

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import org.beerfactory.backend.account.AccountService
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._

trait AccountRoutes {
  def accountService: AccountService

  val accountRoutes = pathprefix("account") {
    path("register") {
      post {
        entity(as[AccountRegisterRequest]) { accountRegistration =>
          onSuccess(accountService.registerAccount(accountRegistration)) {
            case failure:RegistrationFailure => complete(StatusCodes.BadRequest, failure)
            case RegistrationSuccess => complete("success")
          }
        }
      }
    } ~
      path("authenticate") {
        post {
          entity(as[AuthenticateRequest]) { request =>
            onSuccess(accountService.authenticate(request)) {
              case failure:AuthenticateFailure => complete(StatusCodes.BadRequest, failure)
              case success:AuthenticationSuccess =>
                setAuthToken(initAuthToken(success.tokenId, success.userId.toString, s"""{"uid": "${success.userId}"}"""), tokenSignatureKey) {
                  complete("success")
                }
            }
          }
        }
      }
  }
}
