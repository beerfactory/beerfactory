/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package controllers

import java.time.Instant

import org.beerfactory.shared.api.{Error, UserCreateRequest, UserCreateResponse, UserLoginRequest}
import utils.TestHelper
import play.api.libs.json.{JsString, Json}
import play.api.test.FakeRequest
import utils.TestHelper
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._

class UsersControllerSpec extends TestHelper {
  implicit val userCreateRequestFormat  = Json.format[UserCreateRequest]
  implicit val userCreateResponseFormat = Json.format[UserCreateResponse]
  implicit val userLoginReguestFormat   = Json.format[UserLoginRequest]

  "UsersController.create" must {
    "return BadRequest for invalid body" in {
      val Some(result) =
        route(app,
              FakeRequest(POST, routes.UsersController.create().url).withJsonBody(JsString("")))
      status(result) mustEqual BAD_REQUEST
      val Some(error) = Json.parse(contentAsString(result)).asOpt[Error]
      error.id mustEqual "user.create.request.validation"
    }
    "return BadRequest for invalid email" in {
      val request = UserCreateRequest("not@@valid", "password")
      val Some(result) =
        route(app,
              FakeRequest(POST, routes.UsersController.create().url)
                .withJsonBody(Json.toJson(request)))
      status(result) mustEqual BAD_REQUEST
      val Some(error) = Json.parse(contentAsString(result)).asOpt[Error]
      error.id mustEqual "user.create.request.validation"
    }
    "return BadRequest for empty password" in {
      val request = UserCreateRequest("email@test.com", "")
      val Some(result) =
        route(app,
              FakeRequest(POST, routes.UsersController.create().url)
                .withJsonBody(Json.toJson(request)))
      status(result) mustEqual BAD_REQUEST
      val Some(error) = Json.parse(contentAsString(result)).asOpt[Error]
      error.id mustEqual "user.create.request.validation"
    }
    "return Ok for valid request" in {
      val request = UserCreateRequest("valid_request@test.com", "password")
      val Some(result) =
        route(app,
              FakeRequest(POST, routes.UsersController.create().url)
                .withJsonBody(Json.toJson(request)))
      status(result) mustEqual OK
      val Some(response) = Json.parse(contentAsString(result)).asOpt[UserCreateResponse]
      response.id mustBe a[String]
      response.createdAt mustBe defined
      response.createdAt.get mustBe a[Instant]
      response.updatedAt mustBe defined
      response.updatedAt.get mustBe a[Instant]
    }
    "return Bad if user with same email already exists" in {
      val request = UserCreateRequest("first@test.com", "password", Some("first"))

      // Create first user
      val Some(result) =
        route(app,
              FakeRequest(POST, routes.UsersController.create().url)
                .withJsonBody(Json.toJson(request)))
      status(result) mustEqual OK

      // Create second user with same email
      val Some(result2) =
        route(app,
              FakeRequest(POST, routes.UsersController.create().url)
                .withJsonBody(Json.toJson(UserCreateRequest("first@test.com", "xxx"))))
      status(result2) mustEqual CONFLICT
    }

    "return Bad if user with same username already exists" in {
      val request = UserCreateRequest("second@test.com", "password", Some("second"))

      // Create first user
      val Some(result) =
        route(app,
              FakeRequest(POST, routes.UsersController.create().url)
                .withJsonBody(Json.toJson(request)))
      status(result) mustEqual OK

      // Create second user with same username
      val Some(result3) =
        route(
          app,
          FakeRequest(POST, routes.UsersController.create().url)
            .withJsonBody(Json.toJson(UserCreateRequest("me@test.com", "xxx", Some("second")))))
      status(result3) mustEqual CONFLICT
    }
  }

  "UsersController.login" must {
    // Create some user
    val Some(result) =
      route(app,
            FakeRequest(POST, routes.UsersController.create().url).withJsonBody(
              Json.toJson(UserCreateRequest("login@valid.com", "password", Some("success")))))
    status(result) mustEqual OK

    "authorize login request with valid email/password" in {
      val Some(result) =
        route(app,
              FakeRequest(POST, routes.UsersController.login().url)
                .withJsonBody(Json.toJson(UserLoginRequest("login@valid.com", "password"))))
      status(result) mustEqual OK
      val authToken = header("X-Auth-Token", result)
      authToken mustBe defined
    }

    "Un-authorize login request with valid email and invalid password" in {
      val Some(result) =
        route(app,
              FakeRequest(POST, routes.UsersController.login().url)
                .withJsonBody(Json.toJson(UserLoginRequest("login@valid.com", "hacker"))))
      status(result) mustEqual UNAUTHORIZED
      val authToken = header("X-Auth-Token", result)
      authToken mustBe None
    }

    "Un-authorize login request with invalid email/password" in {
      val Some(result) =
        route(app,
              FakeRequest(POST, routes.UsersController.login().url)
                .withJsonBody(Json.toJson(UserLoginRequest("login@fake.com", "password"))))
      status(result) mustEqual UNAUTHORIZED
      val authToken = header("X-Auth-Token", result)
      authToken mustBe None
    }
  }
}
