package org.beerfactory.backend.users

import java.time.OffsetDateTime

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}
import org.beerfactory.backend.users.api._
import org.beerfactory.backend.users.domain.{User, Active}
import org.beerfactory.backend.core.{CryptoActor, UUIDActor}
import org.beerfactory.backend.test.FlatSpecWithDb
import org.scalatest.{BeforeAndAfterAll, Matchers}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

class UsersServiceActorSpec extends FlatSpecWithDb with Matchers with BeforeAndAfterAll with ScalaFutures with IntegrationPatience {
  behavior of "UsersService"

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
    val res = usersService.registerUser(UserRegisterRequest(" ", "password", "some@sample.com")).futureValue
    res shouldEqual RegistrationFailure(List("UsersService.registerUser.username_blank", "UsersService.registerUser.username_minSize"))
  }

  it should "fail register user with blank password" in {
    val res = usersService.registerUser(UserRegisterRequest("login", "", "some@sample.com")).futureValue
    res shouldEqual RegistrationFailure(List("UsersService.registerUser.password_blank", "UsersService.registerUser.password_minSize"))
  }

  it should "fail register user with invalid email" in {
    val res = usersService.registerUser(UserRegisterRequest("login", "password", "invalid@@sample.com")).futureValue
    res shouldEqual RegistrationFailure(List("UsersService.registerUser.email_invalid"))
  }

  it should "fail register user with login and password equal" in {
    val res = usersService.registerUser(UserRegisterRequest("same", "same", "invalid@sample.com")).futureValue
    res shouldEqual RegistrationFailure(List("UsersService.registerUser.restriction_usernameEqualsPassword"))
  }

  it should "succeed register user" in {
    val res = usersService.registerUser(UserRegisterRequest("login", "password", "some@sample.com")).futureValue
    res shouldEqual RegistrationSuccess
  }

  it should "fail register user with login already used" in {
    val res = usersService.registerUser(UserRegisterRequest("samelogin", "password", "samelogin@sample.com")).futureValue
    res shouldEqual RegistrationSuccess
    val res2 = usersService.registerUser(UserRegisterRequest("SameLogin", "xxxxx", "toto@sample.com")).futureValue
    res2 shouldEqual RegistrationFailure(List("UsersService.registerUser.username_alreadyUsed"))
  }

  it should "fail register user with email already used" in {
    val res = usersService.registerUser(UserRegisterRequest("test", "password", "same@sample.com")).futureValue
    res shouldEqual RegistrationSuccess
    val res2 = usersService.registerUser(UserRegisterRequest("otherlogin", "xxxxx", "same@sample.com")).futureValue
    res2 shouldEqual RegistrationFailure(List("UsersService.registerUser.email_alreadyUsed"))
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
    res shouldEqual AuthenticateFailure(List("UsersService.authenticate.user_unknown"))
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
    val res = usersService.registerUser(UserRegisterRequest("notactive", "password", "notactive@sample.com")).futureValue
    res shouldEqual RegistrationSuccess
    val res2 = usersService.authenticate(LoginRequest("notactive", "password")).futureValue
    res2 shouldEqual AuthenticateFailure(List("UsersService.authenticate.user_notYetActive"))
  }
}