/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.frontend.utils

import org.beerfactory.shared.api.UserLoginRequest
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.ext.Ajax.InputData
import org.scalajs.dom.raw.XMLHttpRequest
import org.scalatest._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

import scala.concurrent.Future

class ApiFacadeSpec
    extends WordSpec
    with OptionValues
    with ScalaFutures
    with MustMatchers
    with IntegrationPatience {

  "ApiFacade login" must {
    "return a token for a valid call" in {
      val apiFacade = new ApiFacade() with AjaxCall {
        def doPost(url: String,
                   data: InputData = null,
                   timeout: Int = 0,
                   headers: Map[String, String] = Map.empty,
                   withCredentials: Boolean = false,
                   responseType: String = "") = {
          val rep = new XMLHttpRequest()
          rep.setRequestHeader("X-Auth-Token", "TOKEN")
          Future.successful(rep)
        }
      }

      val value = apiFacade.login(UserLoginRequest("authData", "password")).futureValue
      value mustEqual "TOKEN"
    }
  }
}
