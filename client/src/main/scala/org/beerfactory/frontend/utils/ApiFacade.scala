/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.frontend.utils

import org.beerfactory.frontend.state.AppCircuit
import org.beerfactory.shared.api.{Error, UserLoginRequest}
import org.scalajs.dom.XMLHttpRequest
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.ext.Ajax.InputData
import upickle.default._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait AjaxCall {
  def doPost(url: String,
             data: InputData = null,
             timeout: Int = 0,
             headers: Map[String, String] = Map.empty,
             withCredentials: Boolean = false,
             responseType: String = ""): Future[XMLHttpRequest]
}

class DomExtAjaxCall extends AjaxCall {
  def doPost(url: String,
             data: InputData = null,
             timeout: Int = 0,
             headers: Map[String, String] = Map.empty,
             withCredentials: Boolean = false,
             responseType: String = "") =
    Ajax.post(url, data, timeout, headers, withCredentials, responseType)
}

class ApiFacade() { self: AjaxCall ⇒
  val authToken = AppCircuit.zoom(_.userModel.authToken)

  /**
    * Make an Ajax call + setting Content-Type header to 'application/json'
    */
  private def JsonPost(url: String,
                       data: InputData = null,
                       timeout: Int = 0,
                       headers: Map[String, String] = Map.empty,
                       withCredentials: Boolean = false,
                       responseType: String = "") = {
    val postHeaders = withCredentials match {
      case false ⇒ headers + ("Content-Type" → "application/json")
      case true ⇒
        headers + ("Content-Type" → "application/json") + ("X-Auth-Token" → authToken.value.get)
    }
    doPost(url, data, timeout, postHeaders, withCredentials, responseType)
  }

  /**
    * Make an AJAX call and convert request/response object to/from Json
    */
  private def JsonRequest[REQ: Writer, RESP: Reader](
      url: String,
      request: REQ,
      timeout: Int = 0,
      headers: Map[String, String] = Map.empty,
      withCredentials: Boolean = false,
      responseType: String = ""): Future[Either[Error, RESP]] = {
    JsonPost(url,
             write[REQ](request),
             timeout,
             headers + ("Content-Type" → "application/json"),
             withCredentials,
             responseType).flatMap {
      case xhr ⇒
        Future.successful(Right(read[RESP](xhr.responseText)))
    }.recover { case e: Throwable ⇒ Left(Error("apifacade.json.post.request.error", e.toString)) }
  }

  def login(userLoginRequest: UserLoginRequest): Future[Either[Error, String]] = {
    JsonPost(url = "/api/v1/users/login", data = write(userLoginRequest)).flatMap {
      case xhr ⇒
        val token = xhr.getResponseHeader("X-Auth-Token")
        if (token.isEmpty) Future.successful(Left(read[Error](xhr.responseText)))
        else Future.successful(Right(token))
    }.recover { case e: Throwable ⇒ Left(Error("apifacade.login.recover.error", e.toString)) }
  }
}
