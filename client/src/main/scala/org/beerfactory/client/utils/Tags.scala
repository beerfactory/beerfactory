/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.client.utils

import japgolly.scalajs.react.vdom.all._
import org.beerfactory.client.GlobalStyles
import org.beerfactory.shared.api.UserInfo
import scalacss.Defaults._
import scalacss.ScalaCssReact._

object Tags {
  def avatar(userInfo: UserInfo) = {
    if (userInfo.avatarUrl.isDefined)
      img(GlobalStyles.avatarImage, src := userInfo.avatarUrl.get)
    else {
      val userColor: String =
        (userInfo.email.getBytes().foldLeft(0L)((l, b) ⇒ l + b) % 215).toString
      i(GlobalStyles.avatarLetter, cls := s"avatar-color-$userColor", userInfo.initials)
    }
  }
}
