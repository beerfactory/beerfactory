package controllers

import java.net.URLDecoder
import java.util.UUID
import javax.inject.{Inject, Named}

import actors.MailerActor.Send
import akka.actor.ActorRef
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import controllers.api.Bad
import controllers.api.auth.{ActivationRequest, Token}
import models.auth.services.{AuthTokenService, UserService}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.{JsError, Json}
import play.api.libs.mailer.{Email, MailerClient}
import play.api.mvc.Controller
import utils.auth.DefaultEnv

import scala.concurrent.Future
import scala.language.postfixOps

/**
  * The `Activate Account` controller.
  *
  * @param messagesApi      The Play messages API.
  * @param silhouette       The Silhouette stack.
  * @param userService      The user service implementation.
  * @param authTokenService The auth token service implementation.
  * @param mailerActor     The mailer client actor.
  */
class ActivateAccountController @Inject()(val messagesApi: MessagesApi,
                                          silhouette: Silhouette[DefaultEnv],
                                          userService: UserService,
                                          authTokenService: AuthTokenService,
                                          @Named("mailerActor") mailerActor: ActorRef)
    extends Controller
    with I18nSupport {

  /**
    * Sends an account activation email to the user with the given email.
    *
    * @param email The email address of the user to send the activation mail to.
    * @return The result to display.
    */
  def send(email: String) = silhouette.UnsecuredAction.async { implicit request =>
    val decodedEmail = URLDecoder.decode(email, "UTF-8")
    val loginInfo    = LoginInfo(CredentialsProvider.ID, decodedEmail)

    userService.retrieve(loginInfo).flatMap {
      case Some(user) if !user.activated =>
        authTokenService.create(user.userId).map { authToken =>
          val url = routes.ActivateAccountController.activate(authToken.tokenId).absoluteURL()

          val email = Email(
            subject = Messages("email.activate.account.subject"),
            from = Messages("email.from"),
            to = Seq(decodedEmail),
            bodyText = Some(views.txt.emails.activateAccount(user, url).body),
            bodyHtml = Some(views.html.emails.activateAccount(user, url).body)
          )
          mailerActor ! Send(email)
          Ok(Json.toJson(Token(authToken.tokenId, authToken.expiry, decodedEmail)))
        }
      case None => Future.successful(NotFound)
    }
  }

  /**
    * Activates an account.
    *
    * @param token The token to identify a user.
    * @return The result to display.
    */
  def activate(token: String) = silhouette.UnsecuredAction.async { implicit request =>
    authTokenService.validate(token).flatMap {
      case Some(authToken) =>
        userService.retrieve(authToken.userId).flatMap {
          case Some(user) if user.loginInfo.providerID == CredentialsProvider.ID =>
            userService.save(user.copy(activated = true)).map { _ =>
              Accepted
            }
          case _ =>
            Future.successful(Unauthorized(Json.toJson(Bad("account.activate.invalidUrl"))))
        }
      case None =>
        Future.successful(Unauthorized(Json.toJson(Bad("account.activate.invalidUrl"))))
    }
  }
}
