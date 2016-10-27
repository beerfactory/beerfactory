package controllers

import javax.inject.{Inject, Named}

import actors.MailerActor.Send
import akka.actor.ActorRef
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.api.{LoginInfo, SignUpEvent, Silhouette}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import controllers.api.auth.{RegisterRequest, Token}
import controllers.api.Bad
import models.auth.services.{AuthTokenService, UserService}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.mailer.{Email, MailerClient}
import play.api.mvc.Controller
import play.api.libs.json._
import utils.auth.DefaultEnv

import scala.concurrent.Future

/**
  * The `Sign Up` controller.
  *
  * @param messagesApi            The Play messages API.
  * @param silhouette             The Silhouette stack.
  * @param userService            The user service implementation.
  * @param authInfoRepository     The auth info repository implementation.
  * @param authTokenService       The auth token service implementation.
  * @param avatarService          The avatar service implementation.
  * @param passwordHasherRegistry The password hasher registry.
  * @param mailerActor           The mailer client actor.
  */
class RegisterController @Inject()(val messagesApi: MessagesApi,
                                   silhouette: Silhouette[DefaultEnv],
                                   userService: UserService,
                                   authInfoRepository: AuthInfoRepository,
                                   authTokenService: AuthTokenService,
                                   avatarService: AvatarService,
                                   passwordHasherRegistry: PasswordHasherRegistry,
                                   @Named("mailerActor") mailerActor: ActorRef)
    extends Controller
    with I18nSupport {

  def register = silhouette.UnsecuredAction.async(parse.json) { implicit request =>
    request.body
      .validate[RegisterRequest]
      .map { registerRequest ⇒
        val loginInfo = LoginInfo(CredentialsProvider.ID, registerRequest.email)
        userService.retrieve(loginInfo).flatMap {
          case None =>
            val authInfo =
              passwordHasherRegistry.current.hash(registerRequest.password)
            val fullName = (registerRequest.firstName, registerRequest.lastName) match {
              case (Some(f), Some(l)) => Some(f + " " + l)
              case (Some(f), None)    => Some(f)
              case (None, Some(l))    => Some(l)
              case _                  => None
            }
            for {
              avatar <- avatarService.retrieveURL(registerRequest.email)
              user <- userService.save(loginInfo,
                                       false,
                                       Some(registerRequest.email),
                                       registerRequest.firstName,
                                       registerRequest.lastName,
                                       fullName,
                                       avatar)
              authInfo  <- authInfoRepository.add(loginInfo, authInfo)
              authToken <- authTokenService.create(user.userId)
              result <- Future.successful(
                Ok(Json.toJson(Token(authToken.tokenId, authToken.expiry, registerRequest.email))))
            } yield {
              val url = routes.ActivateAccountController.activate(authToken.tokenId).absoluteURL()
              val email = Email(
                subject = Messages("email.sign.up.subject"),
                from = Messages("email.from"),
                to = Seq(registerRequest.email),
                bodyText = Some(views.txt.emails.register(user, url).body),
                bodyHtml = Some(views.html.emails.register(user, url).body)
              )
              mailerActor ! Send(email)

              silhouette.env.eventBus.publish(SignUpEvent(user, request))
              result
            }
          case Some(user) =>
            /* User already exists */
            Future.successful(Conflict(Json.toJson(Bad("register.user.alreadyExist"))))
        }
      }
      .recoverTotal {
        case error =>
          Future.successful(BadRequest(Json.toJson(Bad(JsError.toJson(error)))))
      }
  }
}
