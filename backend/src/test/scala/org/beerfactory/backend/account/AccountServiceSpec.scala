package org.beerfactory.backend.account

import java.time.OffsetDateTime
import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import org.beerfactory.backend.account.api._
import org.beerfactory.backend.account.domain.{Account, Active}
import org.beerfactory.backend.core.{AccountConfig, CryptoActor, UUIDActor}
import org.beerfactory.backend.test.FlatSpecWithDb
import org.scalatest.{BeforeAndAfterAll, Matchers}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._
import scala.concurrent._

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
    res shouldEqual RegistrationFailure(List("accountRegistration.login.blank", "accountRegistration.login.minSize"))
  }

  it should "fail register account with blank password" in {
    val res = accountService.registerAccount(AccountRegisterRequest("login", "", "some@sample.com")).futureValue
    res shouldEqual RegistrationFailure(List("accountRegistration.password.blank", "accountRegistration.password.minSize"))
  }

  it should "fail register account with invalid email" in {
    val res = accountService.registerAccount(AccountRegisterRequest("login", "password", "invalid@@sample.com")).futureValue
    res shouldEqual RegistrationFailure(List("accountRegistration.email.invalid"))
  }

  it should "fail register account with login and password equal" in {
    val res = accountService.registerAccount(AccountRegisterRequest("same", "same", "invalid@sample.com")).futureValue
    res shouldEqual RegistrationFailure(List("accountRegistration.restriction.loginEqualsPassword"))
  }

  it should "succeed register account" in {
    val res = accountService.registerAccount(AccountRegisterRequest("login", "password", "some@sample.com")).futureValue
    res shouldEqual RegistrationSuccess
  }

  it should "fail register account with login already used" in {
    val res = accountService.registerAccount(AccountRegisterRequest("samelogin", "password", "samelogin@sample.com")).futureValue
    res shouldEqual RegistrationSuccess
    val res2 = accountService.registerAccount(AccountRegisterRequest("SameLogin", "xxxxx", "toto@sample.com")).futureValue
    res2 shouldEqual RegistrationFailure(List("accountRegistration.login.alreadyUsed"))
  }

  it should "fail register account with email already used" in {
    val res = accountService.registerAccount(AccountRegisterRequest("test", "password", "same@sample.com")).futureValue
    res shouldEqual RegistrationSuccess
    val res2 = accountService.registerAccount(AccountRegisterRequest("otherlogin", "xxxxx", "same@sample.com")).futureValue
    res2 shouldEqual RegistrationFailure(List("accountRegistration.email.alreadyUsed"))
  }

  it should "succeed authenticate account" in {
    val acc = accountDao.createAccount("user",
      "20000:mKT6UkNWOEyGqJStFCzf1eWjK2xYx6MQqswq+WLWBas=:YlxDt6zsr6H9u7mQY6nLoTdaITIm6QeOQtGidEPYwlI=",
      "user@sample.com",
      OffsetDateTime.now(),
      Active).futureValue
    acc shouldBe a [Account]
    val res2 = accountService.authenticate(AuthenticateRequest("user", "password")).futureValue
    res2 shouldBe a[AuthenticationSuccess]
  }

  it should "fail authenticate account with invalid login" in {
    val res = accountService.authenticate(AuthenticateRequest("unknown", "password")).futureValue
    res shouldEqual AuthenticateFailure(List("authenticate.account.unknown"))
  }

  it should "fail authenticate account with bad password" in {
    val acc = accountDao.createAccount("user",
      "20000:mKT6UkNWOEyGqJStFCzf1eWjK2xYx6MQqswq+WLWBas=:YlxDt6zsr6H9u7mQY6nLoTdaITIm6QeOQtGidEPYwlI=",
      "user@sample.com",
      OffsetDateTime.now(),
      Active).futureValue
    acc shouldBe a [Account]
    val res = accountService.authenticate(AuthenticateRequest("user", "wrong password")).futureValue
    res shouldEqual AuthenticateFailure(List("checkPassword.password.noMatch"))
  }

  it should "fail authenticate account if not active" in {
    val res = accountService.registerAccount(AccountRegisterRequest("notactive", "password", "notactive@sample.com")).futureValue
    res shouldEqual RegistrationSuccess
    val res2 = accountService.authenticate(AuthenticateRequest("notactive", "password")).futureValue
    res2 shouldEqual AuthenticateFailure(List("accountAuthentication.account.notYetActive"))
  }
}