/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package utils.auth

import javax.inject.Inject

import com.mohiva.play.silhouette.api.actions.SecuredErrorHandler
import org.beerfactory.shared.api.ApiError
import play.api.http.Status
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.{Format, Json}
import play.api.mvc.{RequestHeader, Results}

import scala.concurrent.Future

/**
  * Custom secured error handler.
  *
  * @param messagesApi The Play messages API.
  */
class CustomSecuredErrorHandler @Inject()(val messagesApi: MessagesApi)
    extends SecuredErrorHandler
    with I18nSupport
    with Status
    with Results {

  implicit val errorFormat: Format[ApiError] = Json.format[ApiError]

  /**
    * Called when a user is not authenticated.
    *
    * As defined by RFC 2616, the status code of the response should be 401 Unauthorized.
    *
    * @param request The request header.
    * @return The result to send to the client.
    */
  override def onNotAuthenticated(implicit request: RequestHeader) = {
    Future.successful(Forbidden(Json.toJson(ApiError("not.authenticated", FORBIDDEN))))
  }

  /**
    * Called when a user is authenticated but not authorized.
    *
    * As defined by RFC 2616, the status code of the response should be 403 Forbidden.
    *
    * @param request The request header.
    * @return The result to send to the client.
    */
  override def onNotAuthorized(implicit request: RequestHeader) = {
    Future.successful(Unauthorized(Json.toJson(ApiError("not.authorized", UNAUTHORIZED))))
  }
}
