/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.server.users

import java.time.{OffsetDateTime, ZoneId}
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorRef}
import com.typesafe.scalalogging.StrictLogging
import org.beerfactory.server.users.api._
import org.beerfactory.server.core.{CryptoActor, UUIDActor}
import org.scalactic._
import org.scalactic.Accumulation._
import org.scalactic._
import akka.pattern.ask
import akka.util.Timeout
import org.beerfactory.server.core.UUIDActor.GetUUID
import org.beerfactory.server.core.Validators._
import org.beerfactory.server.models.User
import play.api.Configuration
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class UsersService @Inject()(
  config: Configuration,
  dbConfigProvider: DatabaseConfigProvider,
  @Named("cryptoActor") cryptoActor: ActorRef,
  @Named("uuidActor") uuidActor: ActorRef
  ) extends Actor with HasDatabaseConfigProvider[JdbcProfile] with StrictLogging {

  val usersDao = new UsersDao(driver)

  override def receive = {

  }

  def registerUser(request: UserRegisterRequest): Future[UserRegisterResult] = {
    def checkExistence(): Future[Validation[ErrorMessage]] = {
      for {
        existingLoginOpt <- usersDao.findByLogin(request.login, caseSensitive = false)
        existingEmailOpt <- usersDao.findByEmail(request.email)
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
        val now = OffsetDateTime.now(ZoneId.of("UTC"))
        for {
          passwordHash <- ask(cryptoActor, CryptoActor.HashPassword(request.password)).mapTo[String]
          account <- usersDao.createUser(
            request.login,
            passwordHash,
            request.email,
            false,  //EmailVerified
            now,
            request.nickName,
            request.firstName,
            request.lastName,
            request.locales
          )
        } yield account
        logger.debug(s"Registration success for request: $request")
        Future.successful(RegistrationSuccess)
    }

    logger.debug(s"registerAccount($request)")
    validateRegistrationRequest(request).fold(
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
        case Good(Some(user:User)) => checkPassword(request.password, user.password).flatMap {
              case Fail(err) => Future.successful(AuthenticateFailure(Seq(err)))
              case Pass => ask(uuidActor, GetUUID).mapTo[UUID].flatMap {
                tokenId:UUID => Future.successful(AuthenticationSuccess(user.id, tokenId.toString))
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
