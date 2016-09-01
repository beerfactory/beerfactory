package org.beerfactory.backend.account

import java.time.OffsetDateTime

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}
import org.beerfactory.backend.account.api._
import org.beerfactory.backend.account.domain.{User, Active}
import org.beerfactory.backend.core.{CryptoActor, UUIDActor}
import org.beerfactory.backend.test.FlatSpecWithDb
import org.scalatest.{BeforeAndAfterAll, Matchers}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

class UsersServiceActorSpec extends FlatSpecWithDb with Matchers with BeforeAndAfterAll with ScalaFutures with IntegrationPatience {
  behavior of "UsersServiceActor"

  lazy val actorSystem = ActorSystem()
  import scala.concurrent.ExecutionContext.Implicits.global

  val uuidActor = actorSystem.actorOf(UUIDActor.props(), name = "uuidActor")
  val cryptoActor = actorSystem.actorOf(CryptoActor.props(), name = "cryptoActor")
  val accountDao = new UsersDao(sqlDatabase, uuidActor)
  val serviceConfig = new UsersServiceConfig {
    override def hoconConfig: Config = ConfigFactory.load()
  }
  val usersService = new UsersService(serviceConfig, accountDao, uuidActor, cryptoActor)

  override def afterAll() {
    actorSystem.terminate().futureValue
    super.afterAll
  }

  it should "fail register user with blank login" in {
    val res = usersService.registerUser(UserCreateRequest(" ", "password", "some@sample.com")).futureValue
    res shouldEqual RegistrationFailure(List("accountRegistration.login.blank", "accountRegistration.login.minSize"))
  }

  it should "fail register user with blank password" in {
    val res = usersService.registerUser(UserCreateRequest("login", "", "some@sample.com")).futureValue
    res shouldEqual RegistrationFailure(List("accountRegistration.password.blank", "accountRegistration.password.minSize"))
  }

  it should "fail register user with invalid email" in {
    val res = usersService.registerUser(UserCreateRequest("login", "password", "invalid@@sample.com")).futureValue
    res shouldEqual RegistrationFailure(List("accountRegistration.email.invalid"))
  }

  it should "fail register user with login and password equal" in {
    val res = usersService.registerUser(UserCreateRequest("same", "same", "invalid@sample.com")).futureValue
    res shouldEqual RegistrationFailure(List("accountRegistration.restriction.loginEqualsPassword"))
  }

  it should "succeed register user" in {
    val res = usersService.registerUser(UserCreateRequest("login", "password", "some@sample.com")).futureValue
    res shouldEqual RegistrationSuccess
  }

  it should "fail register user with login already used" in {
    val res = usersService.registerUser(UserCreateRequest("samelogin", "password", "samelogin@sample.com")).futureValue
    res shouldEqual RegistrationSuccess
    val res2 = usersService.registerUser(UserCreateRequest("SameLogin", "xxxxx", "toto@sample.com")).futureValue
    res2 shouldEqual RegistrationFailure(List("accountRegistration.login.alreadyUsed"))
  }

  it should "fail register user with email already used" in {
    val res = usersService.registerUser(UserCreateRequest("test", "password", "same@sample.com")).futureValue
    res shouldEqual RegistrationSuccess
    val res2 = usersService.registerUser(UserCreateRequest("otherlogin", "xxxxx", "same@sample.com")).futureValue
    res2 shouldEqual RegistrationFailure(List("accountRegistration.email.alreadyUsed"))
  }

  it should "succeed authenticate user" in {
    val acc = accountDao.createUser("user",
      "20000:mKT6UkNWOEyGqJStFCzf1eWjK2xYx6MQqswq+WLWBas=:YlxDt6zsr6H9u7mQY6nLoTdaITIm6QeOQtGidEPYwlI=",
      "user@sample.com",
      OffsetDateTime.now(),
      Active).futureValue
    acc shouldBe a [User]
    val res2 = usersService.authenticate(LoginRequest("user", "password")).futureValue
    res2 shouldBe a[AuthenticationSuccess]
  }

  it should "fail authenticate user with invalid login" in {
    val res = usersService.authenticate(LoginRequest("unknown", "password")).futureValue
    res shouldEqual AuthenticateFailure(List("authenticate.account.unknown"))
  }

  it should "fail authenticate user with bad password" in {
    val acc = accountDao.createUser("user",
      "20000:mKT6UkNWOEyGqJStFCzf1eWjK2xYx6MQqswq+WLWBas=:YlxDt6zsr6H9u7mQY6nLoTdaITIm6QeOQtGidEPYwlI=",
      "user@sample.com",
      OffsetDateTime.now(),
      Active).futureValue
    acc shouldBe a [User]
    val res = usersService.authenticate(LoginRequest("user", "wrong password")).futureValue
    res shouldEqual AuthenticateFailure(List("checkPassword.password.noMatch"))
  }

  it should "fail authenticate user if not active" in {
    val res = usersService.registerUser(UserCreateRequest("notactive", "password", "notactive@sample.com")).futureValue
    res shouldEqual RegistrationSuccess
    val res2 = usersService.authenticate(LoginRequest("notactive", "password")).futureValue
    res2 shouldEqual AuthenticateFailure(List("accountAuthentication.account.notYetActive"))
  }
}