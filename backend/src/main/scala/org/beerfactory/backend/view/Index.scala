/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend.view

import scalatags.Text.all._

/**
  * Application root page
  */
object Index extends View {
  val page =
    html (
      head (
        meta(charset:="UTF-8"),
        tag("title")("Beerfactory"),
        meta(content:="'width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no' name='viewport'"),
        css(Assets.webJar("Semantic-UI", "semantic.min.css")),
        css(Assets.public("css/main.css"))
      ),
      body(
        h1("TEST"),
        div(id:="root")

      )
    )
}
