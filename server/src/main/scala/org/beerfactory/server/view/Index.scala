/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.server.view

import scalatags.Text.all._

/**
  * Application root page
  */
class Index(language: String) extends View {
  val page =
    html(
      head(
        meta(charset := "UTF-8"),
        tag("title")("Beerfactory"),
        meta(content := "'width=device-width, initial-scale=1, shrink-to-fit=no'", name := "viewport"),
        css(Assets.webJar("Semantic-UI", "semantic.min.css")),
        css(Assets.webJar("font-awesome", "font-awesome.min.css"))
      ),
      body(
        div(id := "root"),
        //script(src:=Assets.webJar("jquery", "jquery.min.js")),
        //script(src:=Assets.webJar("bootstrap", "bootstrap.min.js")),
        script(s"function acceptLang() { return '$language' } "),
        script(src := "/frontend-fastopt.js"),
        script(src := "/frontend-jsdeps.js"),
        script(src := "/frontend-launcher.js")
      )
    )

  def render = page.render
}

object Index {
  def apply(acceptLang: String) = new Index(acceptLang)
}
