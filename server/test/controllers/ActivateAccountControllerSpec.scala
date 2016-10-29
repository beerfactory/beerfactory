/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package controllers

import java.net.{URLDecoder, URLEncoder}

import controllers.api.Bad
import controllers.api.auth.{ActivationRequest, RegisterRequest, Token}
import models.auth.services.AuthTokenService
import play.api.libs.json.{JsString, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestHelper

class ActivateAccountControllerSpec extends TestHelper {
  val registerApiUrl = "/api/v1/auth/register"
  val emailApiUrl    = "/api/v1/auth/email/"
  val activateApiUrl = "/api/v1/auth/activate/"
  /*
  "ActivateAccountController" must {
    "send an activation email for an existing user request " in {
      //Register a new user
      val Some(result) =
        route(app,
              FakeRequest(POST, registerApiUrl).withJsonBody(
                Json.toJson(RegisterRequest("send@test.com", "password", None, None))))
      status(result) mustEqual OK

      val Some(emailResult) =
        route(app, FakeRequest(POST, emailApiUrl + URLEncoder.encode("send@test.com", "UTF-8")))
      status(emailResult) mustEqual OK
      val Some(token) = Json.parse(contentAsString(emailResult)).asOpt[Token]
      token.email mustEqual "send@test.com"
    }

    "activate account" in {
      //Register a new user
      val Some(result) =
        route(app,
              FakeRequest(POST, registerApiUrl).withJsonBody(
                Json.toJson(RegisterRequest("activate@test.com", "password", None, None))))
      status(result) mustEqual OK
      val Some(token) = Json.parse(contentAsString(result)).asOpt[Token]
      println(token)

      val Some(activationResult) =
        route(app, FakeRequest(POST, activateApiUrl + token.tokenId))
      status(activationResult) mustEqual ACCEPTED
    }

    "fail with UnAuthorized for invalid token" in {
      val Some(activationResult) =
        route(app, FakeRequest(POST, activateApiUrl + "00000"))
      status(activationResult) mustEqual UNAUTHORIZED
      val Some(bad) = Json.parse(contentAsString(activationResult)).asOpt[Bad]
      bad.error mustEqual JsString("account.activate.invalidUrl")
    }
  }
 */
}
