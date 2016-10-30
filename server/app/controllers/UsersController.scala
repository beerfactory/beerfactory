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

import akka.actor.ActorRef
import com.mohiva.play.silhouette.api.{LoginInfo, SignUpEvent, Silhouette}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.auth.services.{AuthTokenService, UserService}
import org.beerfactory.shared.api.{Error, UserCreateRequest, UserCreateResponse}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import utils.auth.DefaultEnv
import play.api.libs.json._
import play.api.mvc.{Controller, Request, Result}
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

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

  /**
    * Handle User creation request
    */
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

  /**
    * Create all information relative to a new user : user information, authentication info
    * @param request User recreation request
    * @param rawRequest raw HTTP request passed to inner functions
    * @return a Future containing a HTTP result
    */
  private def doCreateUser(request: UserCreateRequest, rawRequest: Request[_]): Future[Result] = {
    for {
      loginInfo ← initLogInfo(request)
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
            user <- userService.save(loginInfo,
                                     false,
                                     request.email,
                                     request.userName,
                                     request.firstName,
                                     request.lastName,
                                     request.nickName,
                                     request.locale,
                                     avatar)
            _ <- authInfoRepository.add(loginInfo, authInfo)
          } yield {
            silhouette.env.eventBus.publish(SignUpEvent(user, rawRequest))
            Ok(
              Json.toJson(
                UserCreateResponse(user.userId,
                                   user.createdAt,
                                   user.updatedAt,
                                   user.deletedAt,
                                   user.email,
                                   user.emailVerified,
                                   user.userName,
                                   user.firstName,
                                   user.lastName,
                                   user.nickName,
                                   user.locale,
                                   user.avatarUrl,
                                   user.loginInfo.providerID,
                                   user.loginInfo.providerKey)))
          }
      }
    } yield result
  }

  /**
    * Initialize LoginInfo object from creation request
    * Normally, the object is simply initalized with request data. A user existence check is also performed so a
    * login info duplicate is detected
    * @param request User creation request
    * @return the LoginInfo initialized
    */
  private def initLogInfo(request: UserCreateRequest): Future[LoginInfo] = {
    request.userName match {
      case None ⇒
        // userName is empty. Create user credentials with request email
        Future.successful(LoginInfo(CredentialsProvider.ID, request.email))
      case Some(userName) =>
        // userName is not empty. Retrieve the User and use its email instead of the request email
        // This means that a user already exists with this username
        userService.retrieveByUserName(userName).flatMap {
          case None       ⇒ Future.successful(LoginInfo(CredentialsProvider.ID, request.email))
          case Some(user) ⇒ Future.successful(LoginInfo(CredentialsProvider.ID, user.email))
        }
    }
  }
}
