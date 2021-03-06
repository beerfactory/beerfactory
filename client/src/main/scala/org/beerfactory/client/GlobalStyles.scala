/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.client

import scalacss.Defaults._

object GlobalStyles extends StyleSheet.Inline {
  import dsl._

  val imgLogo = style(
    addClassNames("ui", "image"),
    marginRight(1.5 em),
    width(2.5 em)
  )

  val mainContainer = style(
    addClassNames("ui", "container"),
    marginTop(7 em),
    marginBottom(7 em)
  )

  val leftAlignedErrorMessage = style(addClassNames("ui", "error", "message"), textAlign.left)
  val leftAlignedSuccessMessageWithIcon =
    style(addClassNames("ui", "success", "icon", "message"), textAlign.left)

  val avatarImage = style(
    addClassNames("ui", "avatar", "image"),
    borderRadius(0 rem).important
  )

  /*
  def avatarLetter(color: String) = style(
    addClassNames("avatar", "avatar-plain", "avatar-inverse", "avator-color-" + color),
    marginRight(0.25 em)
  )
   */
  val avatarLetter = style(
    addClassNames("avatar", "avatar-plain", "avatar-inverse"),
    marginRight(0.25 em)
  )
}
