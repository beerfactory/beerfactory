/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{LogoutEvent, Silhouette}
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import play.api.{Configuration, Environment}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import utils.auth.DefaultEnv

class RegistrationController @Inject()(val messagesApi: MessagesApi,
                                       silhouette: Silhouette[DefaultEnv],
                                       socialProviderRegistry: SocialProviderRegistry,
                                       implicit val config: Configuration,
                                       implicit val env: Environment)
    extends Controller
    with I18nSupport {

  /**
    * Handles the index action.
    *
    * @return The result to display.
    */
  def index(any: String) = Action { implicit request =>
    Ok(views.html.registration("app.title"))
  }
}
