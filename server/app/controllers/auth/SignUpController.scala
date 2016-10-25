/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package controllers.auth

import javax.inject.Inject

import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.api.{LoginInfo, SignUpEvent, Silhouette}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import controllers.api.Bad
import controllers.auth.api.SignUp
import controllers.routes
import forms.SignUpForm
import models.auth.services.{AuthTokenService, UserService}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.mailer.{Email, MailerClient}
import play.api.mvc.Controller
import play.libs.Json
import utils.auth.DefaultEnv

import scala.concurrent.Future



class SignUpController @Inject() (
                                   val messagesApi: MessagesApi,
                                   silhouette: Silhouette[DefaultEnv],
                                   userService: UserService,
                                   authInfoRepository: AuthInfoRepository,
                                   authTokenService: AuthTokenService,
                                   avatarService: AvatarService,
                                   passwordHasherRegistry: PasswordHasherRegistry,
                                   mailerClient: MailerClient)
  extends Controller with I18nSupport {

  def signUp = silhouette.UnsecuredAction.async(parse.json) { implicit request =>
    request.body.validate[SignUp].map { signUp ⇒
        val loginInfo = LoginInfo(CredentialsProvider.ID, signUp.email)
        userService.retrieve(loginInfo).flatMap {
          case Some(user) => /* User already exists */
            Future.successful(Conflict(Json.toJson(Bad("signUp.user.alreadyExist"))))
          case None =>
            val authInfo = passwordHasherRegistry.current.hash(signUp.password)
            val fullName = (signUp.firstName, signUp.lastName) match {
              case (Some(f), Some(l)) => Some(f + " " + l)
              case (Some(f), None) => Some(f)
              case (None, Some(l)) => Some(l)
              case _ => None
            }
            for {
              avatar <- avatarService.retrieveURL(signUp.email)
              user <- userService.save(loginInfo, false, Some(signUp.email), signUp.firstName, signUp.lastName, fullName, avatar)
              authInfo <- authInfoRepository.add(loginInfo, authInfo)
              authToken <- authTokenService.create(user.userId)
            } yield {
              val url = routes.ActivateAccountController.activate(authToken.tokenId).absoluteURL()
              mailerClient.send(Email(
                subject = Messages("email.sign.up.subject"),
                from = Messages("email.from"),
                to = Seq(signUp.email),
                bodyText = Some(views.txt.emails.signUp(user, url).body),
                bodyHtml = Some(views.html.emails.signUp(user, url).body)
              ))

              silhouette.env.eventBus.publish(SignUpEvent(user, request))
              //TODO : Future.successfull(Ok(Json.toJson(Token(token = token, expiresOn = authenticator.expirationDate))))
            }
        }
      }
    )
  }

}
