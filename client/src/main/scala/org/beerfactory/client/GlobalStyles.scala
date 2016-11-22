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

  val loginView = style(
    addClassName("ui one column middle aligned center aligned grid")
    //,backgroundColor(c"#DADADA")
  )

  val loginFormColumn = style(
    addClassName("column"),
    maxWidth(450 px)
  )

  val leftAlignedErrorMessage   = style(addClassNames("ui", "error", "message"), textAlign.left)
  val leftAlignedSuccessMessage = style(addClassNames("ui", "success", "message"), textAlign.left)

}
