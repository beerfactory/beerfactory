/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend.users

import java.time.{OffsetDateTime, ZoneId}
import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.ActorRef
import com.typesafe.scalalogging.StrictLogging
import org.beerfactory.backend.users.api._
import org.beerfactory.backend.users.domain._
import org.beerfactory.backend.core.{CryptoActor, UUIDActor}
import org.scalactic._
import org.scalactic.Accumulation._
import org.scalactic._
import akka.pattern.ask
import akka.util.Timeout
import org.beerfactory.backend.core.UUIDActor.GetUUID
import org.beerfactory.backend.core.Validators._

import scala.concurrent.{ExecutionContext, Future}

class UsersService(userConfig: UsersServiceConfig,
                   usersDao: UsersDao,
                   uuidActor: ActorRef,
                   cryptoActor: ActorRef)(implicit ec: ExecutionContext) extends StrictLogging {

  implicit val timeout = Timeout(userConfig.actorWaitTimeout.toMillis, TimeUnit.MILLISECONDS)

  def registerUser(registrationRequest: UserRegisterRequest): Future[UserRegisterResult] = {
    def checkExistence(): Future[Validation[ErrorMessage]] = {
      for {
        existingLoginOpt <- usersDao.findByLogin(registrationRequest.login, caseSensitive = false)
        existingEmailOpt <- usersDao.findByEmail(registrationRequest.email)
      } yield {
        existingLoginOpt.map(_ => Fail("UsersService.registerUser.username_alreadyUsed")).orElse(
          existingEmailOpt.map(_ => Fail("UsersService.registerUser.email_alreadyUsed"))
        ).getOrElse(Pass)
      }
    }

    def register() = checkExistence().flatMap {
      case Fail(error) =>
        logger.debug(s"account existence check failed with errors: $error")
        Future.successful(RegistrationFailure(Seq(error)))
      case Pass =>
        for {
          passwordHash <- ask(cryptoActor, CryptoActor.HashPassword(registrationRequest.password)).mapTo[String]
          account <- usersDao.createUser(registrationRequest.login, passwordHash, registrationRequest.email, OffsetDateTime.now(ZoneId.of("UTC")), NewAccount)
        } yield account
        logger.debug(s"Registration success for request: $registrationRequest")
        Future.successful(RegistrationSuccess)
    }

    logger.debug(s"registerAccount($registrationRequest)")
    validateRegistrationRequest(registrationRequest).fold(
      _ => register(),
      errors => {
        logger.warn(s"Account registration failed with errors: $errors")
        Future.successful(RegistrationFailure(errors.toSeq))
      }
    )
  }

  def authenticate(request: LoginRequest): Future[AuthenticateResult] = {
    def findAccountByLoginOrEmail: Future[Option[User] Or ErrorMessage] = {
      for {
        loginOpt <- usersDao.findByLogin(request.emailOrLogin, caseSensitive = false)
        emailOpt <- usersDao.findByEmail(request.emailOrLogin)
      } yield {
        if(loginOpt.isDefined && emailOpt.isDefined) {
          logger.warn(s"found two occurrences of Account having login or email equals to '${request.emailOrLogin}")
          Bad("UsersService.authenticate.emailOrPassword_notUnique")
        }
        else
          Good(loginOpt orElse emailOpt)
      }
    }

    validateAuthenticateRequest(request).fold(
      _ => findAccountByLoginOrEmail.flatMap {
        case Bad(err: ErrorMessage) => Future.successful(AuthenticateFailure(Seq(err)))
        case Good(None) => Future.successful(AuthenticateFailure(Seq("UsersService.authenticate.user_unknown")))
        case Good(Some(account:User)) => account.status match {
          case NewAccount | ConfirmWait => Future.successful(AuthenticateFailure(Seq("UsersService.authenticate.user_notYetActive")))
          case Disabled => Future.successful(AuthenticateFailure(Seq("UsersService.authenticate.user_disabled")))
          case Active | Confirmed =>
            checkPassword(request.password, account.passwordHash).flatMap {
              case Fail(err) => Future.successful(AuthenticateFailure(Seq(err)))
              case Pass => ask(uuidActor, GetUUID).mapTo[UUID].flatMap {
                tokenId:UUID => Future.successful(AuthenticationSuccess(account.id, tokenId.toString))
              }
            }
        }
      },
      errors => Future.successful(AuthenticateFailure(errors.toSeq))
    )
  }

  private def checkPassword(password: String, hash: String): Future[Validation[ErrorMessage]] = {
    ask(cryptoActor, CryptoActor.CheckPassword(password, hash)).mapTo[Validation[ErrorMessage]]
  }

  private def validateRegistrationRequest(registrationRequest: UserRegisterRequest): Any Or Every[ErrorMessage] = {
    def loginDiffersPassword(errorCode: String)(validated: UserRegisterRequest) = validate(errorCode, validated.login != validated.password)

    def validateLogin = Good(registrationRequest.login) when(
      notBlank("UsersService.registerUser.username_blank"),
      minSize("UsersService.registerUser.username_minSize", userConfig.loginMinSize),
      maxSize("UsersService.registerUser.username_maxSize", userConfig.loginMaxSize)
      )

    def validatePassword = Good(registrationRequest.password) when(
      notBlank("UsersService.registerUser.password_blank"),
      minSize("UsersService.registerUser.password_minSize", userConfig.passwordMinSize),
      maxSize("UsersService.registerUser.password_maxSize", userConfig.passwordMaxSize)
      )

    def validateEmail = Good(registrationRequest.email) when validEmailAddress("UsersService.registerUser.email_invalid")
    def fieldsRestrictions = Good(registrationRequest) when loginDiffersPassword("UsersService.registerUser.restriction_usernameEqualsPassword")

    List(validateLogin, validatePassword, validateEmail, fieldsRestrictions).combined
  }

  private def validateAuthenticateRequest(request: LoginRequest): Any Or Every[ErrorMessage] = {
    def validateEmailOrLogin = Good(request.emailOrLogin) when notBlank("UsersService.authenticate.emailOrLogin_blank")
    def validatePassword = Good(request.password) when notBlank("UsersService.authenticate.password_blank")

    List(validateEmailOrLogin, validatePassword).combined
  }
}
