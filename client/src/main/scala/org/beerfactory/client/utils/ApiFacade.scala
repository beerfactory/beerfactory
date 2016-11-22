/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.client.utils

import cats.data.Xor
import org.beerfactory.client.state.AppCircuit
import org.beerfactory.shared.api._
import org.scalajs.dom.XMLHttpRequest
import org.scalajs.dom.ext.{Ajax, AjaxException}
import org.scalajs.dom.ext.Ajax.InputData
import slogging.LazyLogging

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait ApiFacade {
  def login(userLoginRequest: UserLoginRequest): Future[Either[ApiError, String]]
  def getCurrentUser: Future[Either[ApiError, UserCurrentResponse]]
}

object AjaxApiFacade extends ApiFacade with LazyLogging {
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
  implicit val userCreateRequestEncoder: Encoder[UserCreateRequest]     = deriveEncoder
  implicit val userCreateResponseDecoder: Decoder[UserCreateResponse]   = deriveDecoder

  private val authTokenReader = AppCircuit.zoom(_.userModel.authToken)

  private def parseError(xhr: XMLHttpRequest): ApiError = {
    parse(xhr.responseText).fold(
      failure =>
        ApiError("apifacade.json.parse", Seq(failure.toString, xhr.responseText), xhr.status),
      json =>
        json
          .as[ApiError]
          .getOrElse(ApiError("apifacade.json.decode", Seq(xhr.responseText), xhr.status)))
  }

  def register(
      userCreateRequest: UserCreateRequest): Future[Either[ApiError, UserCreateResponse]] = {
    JsonPost(url = "/api/v1/users/", data = userCreateRequest.asJson.noSpaces).flatMap { xhr ⇒
      xhr.status match {
        case 200 ⇒
          parse(xhr.responseText).fold(
            failure ⇒
              Future.successful(
                Left(
                  ApiError("apifacade.json.parse",
                           Seq(failure.toString, xhr.responseText),
                           xhr.status))),
            json =>
              json
                .as[UserCreateResponse]
                .fold(
                  failure ⇒
                    Future.successful(
                      Left(
                        ApiError("apifacade.json.decode",
                                 Seq(failure.toString(), xhr.responseText),
                                 xhr.status))),
                  resp ⇒ Future.successful(Right(resp))
              )
          )
        case _ ⇒
          Future.successful(Left(parseError(xhr)))
      }
    }.recover {
      case aje: AjaxException ⇒
        Left(parseError(aje.xhr))
      case e: Throwable ⇒ Left(ApiError("apifacade.register.recover.error", e.toString))
    }
  }

  def login(userLoginRequest: UserLoginRequest): Future[Either[ApiError, String]] = {
    JsonPost(url = "/api/v1/users/login", data = userLoginRequest.asJson.noSpaces).flatMap {
      case xhr ⇒
        val token = xhr.getResponseHeader("X-Auth-Token")
        if (token.isEmpty)
          Future.successful(Left(parseError(xhr)))
        else Future.successful(Right(token))
    }.recover {
      case aje: AjaxException ⇒
        Left(parseError(aje.xhr))
      case e: Throwable ⇒ Left(ApiError("apifacade.login.recover.error", e.toString))
    }
  }

  def getCurrentUser: Future[Either[ApiError, UserCurrentResponse]] = {
    jsonGet(url = "/api/v1/users/current", withCredentials = true).flatMap { xhr ⇒
      xhr.status match {
        case 200 ⇒
          parse(xhr.responseText).fold(
            failure =>
              Future.successful(
                Left(
                  ApiError("apifacade.json.parse",
                           Seq(failure.toString, xhr.responseText),
                           xhr.status))),
            json =>
              json
                .as[UserCurrentResponse]
                .fold(
                  failure ⇒
                    Future.successful(
                      Left(
                        ApiError("apifacade.json.decode",
                                 Seq(failure.toString(), xhr.responseText),
                                 xhr.status))),
                  resp ⇒ Future.successful(Right(resp))
              )
          )
        case status: Int ⇒
          Future.successful(Left(parseError(xhr)))
      }
    }.recover {
      case aje: AjaxException ⇒
        Left(parseError(aje.xhr))
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
        if (authTokenReader.value.isReady)
          headers + ("Content-Type" → "application/json") + ("X-Auth-Token" → authTokenReader.value
            .getOrElse(""))
        else {
          logger.warn(
            "Ajax call with creadentials but authToken not ready, X-Auth-Token header ignored")
          headers + ("Content-Type" → "application/json")
        }
    }
    Ajax.get(url, data, timeout, getHeaders, withCredentials, responseType)
  }
}
