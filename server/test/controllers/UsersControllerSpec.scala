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

import org.beerfactory.shared.api.{Error, UserCreateRequest, UserCreateResponse}
import utils.TestHelper
import play.api.libs.json.{JsString, Json}
import play.api.test.FakeRequest
import utils.TestHelper
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._

class UsersControllerSpec extends TestHelper {
  "UsersController request validation" must {
    "return BadRequest for invalid body" in {
      val Some(result) =
        route(app,
              FakeRequest(POST, routes.UsersController.create().url).withJsonBody(JsString("")))
      status(result) mustEqual BAD_REQUEST
      val Some(error) = Json.parse(contentAsString(result)).asOpt[Error]
      error.id mustEqual "create.user.request.validation"
    }
    "return BadRequest for invalid email" in {
      val request = UserCreateRequest("not@@valid", "password")
      val Some(result) =
        route(app,
              FakeRequest(POST, routes.UsersController.create().url)
                .withJsonBody(Json.toJson(request)))
      status(result) mustEqual BAD_REQUEST
      val Some(error) = Json.parse(contentAsString(result)).asOpt[Error]
      error.id mustEqual "create.user.request.validation"
    }
    "return BadRequest for empty password" in {
      val request = UserCreateRequest("email@test.com", "")
      val Some(result) =
        route(app,
              FakeRequest(POST, routes.UsersController.create().url)
                .withJsonBody(Json.toJson(request)))
      status(result) mustEqual BAD_REQUEST
      val Some(error) = Json.parse(contentAsString(result)).asOpt[Error]
      error.id mustEqual "create.user.request.validation"
    }
  }

  "UsersController" must {
    "return Ok for valid request" in {
      val request = UserCreateRequest("valid_request@test.com", "password")
      val Some(result) =
        route(app,
              FakeRequest(POST, routes.UsersController.create().url)
                .withJsonBody(Json.toJson(request)))
      status(result) mustEqual OK
      val Some(response) = Json.parse(contentAsString(result)).asOpt[UserCreateResponse]
      print(response)
      response.id mustBe a[String]
      response.createdAt mustBe a[Some[Instant]]
      response.updatedAt mustBe a[Some[Instant]]
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
}
