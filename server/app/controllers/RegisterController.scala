package controllers

import javax.inject.{Inject, Named}

import actors.MailerActor.Send
import akka.actor.ActorRef
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers._
import forms.RegisterForm
import models.auth.services.{AuthTokenService, UserService}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.mailer.Email
import play.api.mvc.Controller
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

  /**
    * Views the `Sign Up` page.
    *
    * @return The result to display.
    */
  def view = silhouette.UnsecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.register(RegisterForm.form)))
  }

  /**
    * Handles the submitted form.
    *
    * @return The result to display.
    */
  def submit = silhouette.UnsecuredAction.async { implicit request =>
    RegisterForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.register(form))),
      data => {
        val result = Redirect(routes.RegisterController.view())
          .flashing("info" -> Messages("sign.up.email.sent", data.email))
        val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
        userService.retrieve(loginInfo).flatMap {
          case Some(user) =>
            val url = routes.RegisterController.view().absoluteURL()
            val email = Email(
              subject = Messages("email.already.signed.up.subject"),
              from = Messages("email.from"),
              to = Seq(data.email),
              bodyText = Some(views.txt.emails.alreadySignedUp(user, url).body),
              bodyHtml = Some(views.html.emails.alreadySignedUp(user, url).body)
            )
            mailerActor ! Send(email)

            Future.successful(result)
          case None =>
            val authInfo = passwordHasherRegistry.current.hash(data.password)
            for {
              avatar <- avatarService.retrieveURL(data.email)
              user <- userService.save(loginInfo,
                                       false,
                                       Some(data.email),
                                       Some(data.firstName),
                                       Some(data.lastName),
                                       Some(data.firstName + " " + data.lastName),
                                       avatar)
              authInfo  <- authInfoRepository.add(loginInfo, authInfo)
              authToken <- authTokenService.create(user.userId)
            } yield {
              val url = routes.ActivateAccountController.activate(authToken.tokenId).absoluteURL()
              val email = Email(
                subject = Messages("email.sign.up.subject"),
                from = Messages("email.from"),
                to = Seq(data.email),
                bodyText = Some(views.txt.emails.register(user, url).body),
                bodyHtml = Some(views.html.emails.register(user, url).body)
              )
              mailerActor ! Send(email)

              silhouette.env.eventBus.publish(SignUpEvent(user, request))
              result
            }
        }
      }
    )
  }
}
