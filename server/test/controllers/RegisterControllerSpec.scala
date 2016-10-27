/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package controllers

import play.api.test.FakeRequest
import utils.TestHelper
import play.api.test.Helpers._

class RegisterControllerSpec extends TestHelper {

  "RegisterController" must {
    "return BadRequest for invalid body" in {
      val Some(result) = route(app, addCsrfToken(FakeRequest(POST, "/register")))
      status(result) mustEqual BAD_REQUEST
    }

    "return token for a valid request" in {
      val Some(result) =
        route(app,
              addCsrfToken(
                FakeRequest(POST, "/register").withFormUrlEncodedBody(
                  "firstName" -> "test_firstName",
                  "lastName"  -> "test_lastName",
                  "email"     -> "email@test.com",
                  "password"  -> "password"
                )))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustEqual Some(routes.SignInController.view().url)
    }
    /*

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
   */
  }
}
