package org.beerfactory.backend.account

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import org.beerfactory.backend.account.api._
import org.beerfactory.backend.core.{AccountConfig, CryptoActor, UUIDActor}
import org.beerfactory.backend.test.FlatSpecWithDb
import org.scalatest.{BeforeAndAfterAll, Matchers}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

import scala.concurrent.{Await, ExecutionContext}

class AuthServiceActorSpec extends FlatSpecWithDb with Matchers with BeforeAndAfterAll with ScalaFutures with IntegrationPatience {
  behavior of "AuthServiceActor"

  lazy val actorSystem = ActorSystem()
  import scala.concurrent.ExecutionContext.Implicits.global

  val uuidActor = actorSystem.actorOf(UUIDActor.props(), name = "uuidActor")
  val cryptoActor = actorSystem.actorOf(CryptoActor.props(), name = "cryptoActor")
  val accountDao = new AccountDao(sqlDatabase, uuidActor)
  val serviceConfig = new AccountConfig {
    override def hoconConfig: Config = ConfigFactory.load()
  }
  val accountService = new AccountService(serviceConfig, accountDao, uuidActor, cryptoActor)

  override def afterAll() {
    actorSystem.terminate().futureValue
    super.afterAll
  }

  it should "fail register account with blank login" in {
    val res = accountService.registerAccount(AccountRegisterRequest(" ", "password", "some@sample.com")).futureValue
    res shouldEqual RegistrationFailure(List("userRegistration.login.blank", "userRegistration.login.minSize"))
  }

  it should "fail register account with blank password" in {
    val res = accountService.registerAccount(AccountRegisterRequest("login", "", "some@sample.com")).futureValue
    res shouldEqual RegistrationFailure(List("userRegistration.password.blank", "userRegistration.password.minSize"))
  }

  it should "fail register account with invalid email" in {
    val res = accountService.registerAccount(AccountRegisterRequest("login", "password", "invalid@@sample.com")).futureValue
    res shouldEqual RegistrationFailure(List("userRegistration.email.invalid"))
  }

  it should "fail register account with login and password equal" in {
    val res = accountService.registerAccount(AccountRegisterRequest("same", "same", "invalid@sample.com")).futureValue
    res shouldEqual RegistrationFailure(List("userRegistration.restriction.loginEqualsPassword"))
  }

  it should "succeed register account" in {
    val res = accountService.registerAccount(AccountRegisterRequest("login", "password", "some@sample.com")).futureValue
    res shouldEqual RegistrationSuccess
  }

  it should "fail register account with login already used" in {
    accountService.registerAccount(AccountRegisterRequest("samelogin", "password", "samelogin@sample.com")).onSuccess {
      case res => {
        res shouldEqual RegistrationSuccess
        val res2 = accountService.registerAccount(AccountRegisterRequest("SameLogin", "xxxxx", "toto@sample.com")).futureValue
        res2 shouldEqual RegistrationFailure(List("userRegistration.login.alreadyUsed"))
      }
    }
  }

  it should "fail register account with email already used" in {
    accountService.registerAccount(AccountRegisterRequest("samelogin", "password", "same@sample.com")).onSuccess {
      case res => {
        res shouldEqual RegistrationSuccess
        val res2 = accountService.registerAccount(AccountRegisterRequest("otherlogin", "xxxxx", "same@sample.com")).futureValue
        res2 shouldEqual RegistrationFailure(List("userRegistration.email.alreadyUsed"))
      }
    }
  }

  it should "succeed authenticate account" in {
    accountService.registerAccount(AccountRegisterRequest("user", "password", "user@password.com")).onSuccess {
      case res => {
        res shouldEqual RegistrationSuccess
        val res2 = accountService.authenticate(AuthenticateRequest("user", "password")).futureValue
        res2 shouldBe a[AuthenticationSuccess]
      }
    }
  }

  it should "succeed authenticate account with invalid login" in {
    val res = accountService.authenticate(AuthenticateRequest("unknown", "password")).futureValue
    res shouldEqual AuthenticateFailure(List("authenticate.account.unknown"))
  }

  it should "fail authenticate account with bad password" in {
    val res = accountService.authenticate(AuthenticateRequest("user", "wrong password")).futureValue
    res shouldEqual AuthenticateFailure(List("checkPassword.password.noMatch"))
  }

  it should "fail authenticate account if not active" in {
    val res = accountService.registerAccount(AccountRegisterRequest("notactive", "password", "notactive@sample.com")).futureValue
    res shouldEqual RegistrationSuccess
    val res2 = accountService.authenticate(AuthenticateRequest("notactive", "password")).futureValue
    res2 shouldEqual AuthenticateFailure(List("authenticate.account.notYetActive"))
  }
}