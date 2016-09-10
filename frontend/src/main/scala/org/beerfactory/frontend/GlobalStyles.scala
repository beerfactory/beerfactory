/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.frontend

import scalacss.Defaults._
import scalacss.internal.Attrs

object GlobalStyles extends StyleSheet.Inline {
  import dsl._

  val imgLogo = style(
    unsafeExt(".ui.menu .item img" + _) (
      Attrs.marginRight(1.5 em)
    )
  )
}
