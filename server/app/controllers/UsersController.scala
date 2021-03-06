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
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.{LoginEvent, LoginInfo, SignUpEvent, Silhouette}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.{Credentials, PasswordHasherRegistry}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.auth.User
import models.auth.services.{AuthTokenService, UserService}
import org.beerfactory.shared.api._
import org.scalactic._
import org.scalactic.Accumulation._
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import utils.auth.DefaultEnv
import play.api.libs.json._
import play.api.mvc.{Controller, Request, RequestHeader, Result}
import play.api.libs.concurrent.Execution.Implicits._
import org.beerfactory.shared.utils.Validators._

import scala.concurrent.Future

class UsersController @Inject()(val messagesApi: MessagesApi,
                                silhouette: Silhouette[DefaultEnv],
                                userService: UserService,
                                authInfoRepository: AuthInfoRepository,
                                authTokenService: AuthTokenService,
                                credentialsProvider: CredentialsProvider,
                                avatarService: AvatarService,
                                configuration: Configuration,
                                passwordHasherRegistry: PasswordHasherRegistry,
                                @Named("mailerActor") mailerActor: ActorRef)
    extends Controller
    with I18nSupport {

  implicit val userInfoFormat             = Json.format[UserInfo]
  implicit val userCreateRequestFormat    = Json.format[UserCreateRequest]
  implicit val userCreateResponseFormat   = Json.format[UserCreateResponse]
  implicit val userLoginReguestFormat     = Json.format[UserLoginRequest]
  implicit val userCurrentFormat          = Json.format[UserCurrentResponse]
  implicit val errorFormat: Format[ApiError] = Json.format[ApiError]

  /**
    * Handle User creation request
    */
  def create = silhouette.UnsecuredAction.async(parse.json) { implicit rawRequest =>
    rawRequest.body
      .validate[UserCreateRequest]
      .fold(
        invalid ⇒
          Future.successful(BadRequest(Json.toJson(
            ApiError("user.create.request.validation", JsError.toJson(invalid).toString, BAD_REQUEST)))),
        request ⇒
          validateUserCreateRequest(request).fold(
            request ⇒ doCreateUser(request),
            errors ⇒
              Future.successful(BadRequest(Json.toJson(
                ApiError("user.create.request.validation", errors.toSeq.map(e ⇒ e.toString), BAD_REQUEST)))))
      )
  }

  def login = silhouette.UnsecuredAction.async(parse.json) { implicit rawRequest ⇒
    rawRequest.body
      .validate[UserLoginRequest]
      .fold(
        invalid ⇒
          Future.successful(BadRequest(Json.toJson(
            ApiError("user.login.request.validation", JsError.toJson(invalid).toString, BAD_REQUEST)))),
        request ⇒ {
          val credentials = Credentials(request.authData, request.password)
          credentialsProvider
            .authenticate(credentials)
            .flatMap {
              loginInfo =>
                userService.retrieve(loginInfo).flatMap {
                  case Some(user) =>
                    silhouette.env.authenticatorService.create(loginInfo).flatMap {
                      authenticator =>
                        silhouette.env.eventBus.publish(LoginEvent(user, rawRequest))
                        silhouette.env.authenticatorService.init(authenticator).flatMap { v =>
                          silhouette.env.authenticatorService.embed(v, Ok)
                        }
                    }
                  case None =>
                    Future.successful(
                      Unauthorized(Json.toJson(ApiError("user.login.notfound", UNAUTHORIZED))))
                }
            }
            .recover {
              case e: ProviderException =>
                Unauthorized(Json.toJson(ApiError("user.login.invalid.credentials", UNAUTHORIZED)))
            }
        }
      )
  }

  def currentUser = silhouette.SecuredAction { implicit request =>
    request.identity match {
      case user: User ⇒
        Ok(
          Json.toJson(
            UserCurrentResponse(
              userInfo = UserInfo(user.userId,
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
                                  user.loginInfo.providerKey))))
      case _ => BadRequest(Json.toJson(ApiError("user.current.invalid", BAD_REQUEST)))
    }
  }

  /**
    * Create all information relative to a new user : user information, authentication info
    *
    * @param request    User recreation request
    * @param rawRequest raw HTTP request passed to inner functions
    * @return a Future containing a HTTP result
    */
  private def doCreateUser(request: UserCreateRequest)(
      implicit rawRequest: RequestHeader): Future[Result] = {
    for {
      loginInfo ← initLogInfo(request)
      user      <- userService.retrieve(loginInfo)
      result ← user match {
        case Some(_) =>
          /* User already exists */
          Future.successful(
            Conflict(Json.toJson(
              ApiError("create.user.alreadyExist", Messages("create.user.alreadyExist"), CONFLICT))))
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
                UserCreateResponse(
                  userInfo = UserInfo(user.userId,
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
                                      user.loginInfo.providerKey))))
          }
      }
    } yield result
  }

  /**
    * Initialize LoginInfo object from creation request
    * Normally, the object is simply initalized with request data. A user existence check is also performed so a
    * login info duplicate is detected
    *
    * @param request User creation request
    * @return the LoginInfo initialized
    */
  private def initLogInfo(request: UserCreateRequest): Future[LoginInfo] = {
      // Retrieve the User and use its email instead of the request email
      // This means that a user already exists with this username
      userService.retrieveByUserName(request.userName).flatMap {
        case None       ⇒ Future.successful(LoginInfo(CredentialsProvider.ID, request.email))
        case Some(user) ⇒ Future.successful(LoginInfo(CredentialsProvider.ID, user.email))
      }
  }

  private def validateUserCreateRequest(
      request: UserCreateRequest): UserCreateRequest Or Every[ErrorMessage] = {
    for {
      email    ← Good(request.email) when validEmailAddress("user.create.request.email.invalid")
      password ← Good(request.password) when notEmpty("user.create.request.password.empty")
      username ← Good(request.userName) when notEmpty("user.create.request.username.empty")
    } yield request.copy(email = email, password = password, userName = username)
  }
}
