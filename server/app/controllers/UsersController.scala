/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package controllers

import javax.inject.{Inject, Named}

import actors.MailerActor.Send
import akka.actor.ActorRef
import com.mohiva.play.silhouette.api.{LoginInfo, SignUpEvent, Silhouette}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import controllers.api.auth.Token
import models.auth.User
import models.auth.services.{AuthTokenService, UserService}
import org.beerfactory.shared.api.{Error, UserCreateRequest}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import utils.auth.DefaultEnv
import play.api.libs.json._
import play.api.libs.mailer.Email
import play.api.mvc.{Controller, Request, Result}
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future
import scala.util.{Failure, Success}

class UsersController @Inject()(val messagesApi: MessagesApi,
                                silhouette: Silhouette[DefaultEnv],
                                userService: UserService,
                                authInfoRepository: AuthInfoRepository,
                                authTokenService: AuthTokenService,
                                avatarService: AvatarService,
                                passwordHasherRegistry: PasswordHasherRegistry,
                                @Named("mailerActor") mailerActor: ActorRef)
    extends Controller
    with I18nSupport {

  def create = silhouette.UnsecuredAction.async(parse.json) { rawRequest =>
    rawRequest.body
      .validate[UserCreateRequest]
      .fold(
        invalid ⇒
          Future.successful(
            BadRequest(Json.toJson(
              Error("create.user.request.validation", JsError.toJson(invalid), BAD_REQUEST)))),
        request ⇒ doCreateUser(request, rawRequest)
      )
  }

  private def doCreateUser(request: UserCreateRequest, rawRequest: Request[_]): Future[Result] = {
    for {
      loginInfo ← getLoginInfo(request)
      user      <- userService.retrieve(loginInfo)
      result ← user match {
        case Some(_) =>
          /* User already exists */
          Future.successful(
            Conflict(Json.toJson(
              Error("create.user.alreadyExist", Messages("create.user.alreadyExist"), CONFLICT))))
        case None =>
          val authInfo = passwordHasherRegistry.current.hash(request.password)
          for {
            avatar <- avatarService.retrieveURL(request.email)
            newUser <- userService.save(loginInfo,
                                        false,
                                        request.email,
                                        request.userName,
                                        request.firstName,
                                        request.lastName,
                                        request.nickName,
                                        request.locale,
                                        avatar)
            authInfo  <- authInfoRepository.add(loginInfo, authInfo)
            authToken <- authTokenService.create(newUser.userId)
          } yield {
            silhouette.env.eventBus.publish(SignUpEvent(newUser, rawRequest))
            Ok(Json.toJson(Token(authToken.tokenId, authToken.expiry, newUser.email)))
          }
      }
    } yield result
  }

  private def getLoginInfo(request: UserCreateRequest): Future[LoginInfo] = {
    request.userName match {
      case None ⇒
        // userName is empty. Create user credentials with request email
        Future.successful(LoginInfo(CredentialsProvider.ID, request.email))
      case Some(userName) =>
        // userName is not empty. Retrieve the User and use its email insted of the request email
        // This means that a user already exists with this username
        userService.retrieveByUserName(userName).flatMap {
          case None       ⇒ Future.successful(LoginInfo(CredentialsProvider.ID, request.email))
          case Some(user) ⇒ Future.successful(LoginInfo(CredentialsProvider.ID, user.email))
        }
    }
  }
}
