package org.beerfactory.frontend.pages

import diode.react.ModelProxy
import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react.extra.router.RouterCtl
import org.beerfactory.frontend.state.UserModel
import japgolly.scalajs.react.vdom.all._
import org.beerfactory.frontend.components.Commons._

/**
  * Created by njouanin on 07/11/16.
  */
object LoginPage {
  case class Props(router: RouterCtl[Page], proxy: ModelProxy[UserModel])

  // format: off
  private val LoginForm = ReactComponentB[Props]("LoginPage").render_P { props =>
    form(cls:= "ui column large form attached segment",
    )
  }.build
  // format: on

  private val component =
    // format: off
    ReactComponentB[Props]("LoginPage").render_P { props =>
      div(cls := "ui three column centered grid",
        GridRow(H1Header("Login to Beerfactory")),
        GridRow(
          div(cls := "column",
            LoginForm(props)
          )
        )
      )
    }.build
  // format: on

  def apply(router: RouterCtl[Page], proxy: ModelProxy[UserModel]) =
    component(Props(router, proxy))
}
