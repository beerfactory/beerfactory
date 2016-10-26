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

import controllers.api.auth.{SignUp, Token}
import play.api.libs.json.{JsString, Json}
import play.api.test.FakeRequest
import utils.TestHelper
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._

class SignUpControllerSpec extends TestHelper {

  "SignUpController" must {
    "return BadRequest for invalid body" in {
      val Some(result) = route(app, FakeRequest(POST, "/auth/signup").withJsonBody(JsString("")))
      status(result) mustEqual BAD_REQUEST
    }

    "return token for a valid request" in {
      val signUp = SignUp("email@test.com", "password", None, None)
      val Some(result) =
        route(app, FakeRequest(POST, "/auth/signup").withJsonBody(Json.toJson(signUp)))

      status(result) mustEqual OK
      val Some(token) = Json.parse(contentAsString(result)).asOpt[Token]
      Instant.now().isBefore(token.expiry) mustBe true
    }

    "return error if user already exists (same email)" in {
      val signUp = SignUp("some@test.com", "password", None, None)
      val Some(result) =
        route(app, FakeRequest(POST, "/auth/signup").withJsonBody(Json.toJson(signUp)))
      status(result) mustEqual OK

      val Some(result2) =
        route(app, FakeRequest(POST, "/auth/signup").withJsonBody(Json.toJson(signUp)))
      status(result2) mustEqual CONFLICT
    }
  }
}
