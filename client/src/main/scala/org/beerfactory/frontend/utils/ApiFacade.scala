/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.frontend.utils

import cats.data.Xor
import org.beerfactory.frontend.state.AppCircuit
import org.beerfactory.shared.api.{ApiError, UserCurrentResponse, UserInfo, UserLoginRequest}
import org.scalajs.dom.XMLHttpRequest
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.ext.Ajax.InputData

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait ApiFacade {
  def login(userLoginRequest: UserLoginRequest): Future[Either[ApiError, String]]
  def getCurrentUser: Future[Either[ApiError, UserCurrentResponse]]
}

object AjaxApiFacade extends ApiFacade {
  import io.circe._, io.circe.generic.semiauto._
  import io.circe.syntax._
  import io.circe.parser._

  import java.time.Instant
  // import java.time.Instant

  new String()

  implicit val encodeInstant: Encoder[Instant] =
    Encoder.encodeString.contramap[Instant](i => i.toString)
  implicit val decodeInstant: Decoder[Instant] = Decoder.decodeString.emap { str =>
    Xor.catchNonFatal(Instant.parse(str)).leftMap(t => "Instant")
  }
  implicit val userInfoDecoder: Decoder[UserInfo]                       = deriveDecoder
  implicit val userInfoEncoder: Encoder[UserInfo]                       = deriveEncoder
  implicit val userLoginRequestDecoder: Decoder[UserLoginRequest]       = deriveDecoder
  implicit val userLoginRequestEncoder: Encoder[UserLoginRequest]       = deriveEncoder
  implicit val userCurrentResponseDecoder: Decoder[UserCurrentResponse] = deriveDecoder
  implicit val userCurrentResponseEncoder: Encoder[UserCurrentResponse] = deriveEncoder
  implicit val errorDecoder: Decoder[ApiError]                          = deriveDecoder
  implicit val errorEncoder: Encoder[ApiError]                          = deriveEncoder

  private val authTokenReader = AppCircuit.zoom(_.userModel.authToken)

  private def parseError(xhr: XMLHttpRequest): ApiError = {
    parse(xhr.responseText).fold(
      failure => ApiError("apifacade.json.parse", Seq(failure.toString), xhr.status),
      json =>
        json
          .as[ApiError]
          .getOrElse(ApiError("apifacade.json.decode", Seq(xhr.responseText), xhr.status)))
  }

  def login(userLoginRequest: UserLoginRequest): Future[Either[ApiError, String]] = {
    JsonPost(url = "/api/v1/users/login", data = userLoginRequest.asJson.noSpaces).flatMap {
      case xhr ⇒
        val token = xhr.getResponseHeader("X-Auth-Token")
        if (token.isEmpty)
          Future.successful(Left(parseError(xhr)))
        else Future.successful(Right(token))
    }.recover { case e: Throwable ⇒ Left(ApiError("apifacade.login.recover.error", e.toString)) }
  }

  def getCurrentUser: Future[Either[ApiError, UserCurrentResponse]] = {
    jsonGet(url = "/api/v1/users/current").flatMap { xhr ⇒
      xhr.status match {
        case 200 ⇒
          parse(xhr.responseText).fold(
            failure =>
              Future.successful(
                Left(ApiError("apifacade.json.parse", Seq(failure.toString), xhr.status))),
            json =>
              json
                .as[UserCurrentResponse]
                .fold(
                  failure ⇒
                    Future.successful(Left(
                      ApiError("apifacade.json.decode", Seq(failure.toString()), xhr.status))),
                  resp ⇒ Future.successful(Right(resp))
              )
          )
        case status: Int ⇒
          Future.successful(Left(parseError(xhr)))
      }
    }.recover {
      case e: Throwable ⇒ Left(ApiError("apifacade.json.post.request.error", e.toString))
    }
  }

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
        headers + ("Content-Type" → "application/json") + ("X-Auth-Token" → authTokenReader.value.get)
    }
    Ajax.post(url, data, timeout, postHeaders, withCredentials, responseType)
  }

  /**
    * Make an Ajax call + setting Content-Type header to 'application/json'
    */
  private def jsonGet(url: String,
                      data: InputData = null,
                      timeout: Int = 0,
                      headers: Map[String, String] = Map.empty,
                      withCredentials: Boolean = false,
                      responseType: String = "") = {
    val getHeaders = withCredentials match {
      case false ⇒ headers + ("Content-Type" → "application/json")
      case true ⇒
        headers + ("Content-Type" → "application/json") + ("X-Auth-Token" → authTokenReader.value.get)
    }
    Ajax.get(url, data, timeout, getHeaders, withCredentials, responseType)
  }
}
