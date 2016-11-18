package org.beerfactory.frontend.pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.all._
import org.beerfactory.frontend.components.Commons._
import org.beerfactory.frontend.components.RegisterForm

/**
  * Created by njouanin on 18/11/16.
  */
object RegisterPage {
  case class Props(router: RouterCtl[Page])

  case class State()

  class Backend(scope: BackendScope[Props, State]) {

    def render(props: Props, state: State) =
      div(cls := "ui three column centered grid",
          GridRow(H1Header("Register to Beerfactory")),
          GridRow(RegisterForm(RegisterForm.Props(props.router))))
  }

  private val component =
    ReactComponentB[Props]("RegisterPage").initialState(State()).renderBackend[Backend].build

  def apply(router: RouterCtl[Page]) =
    component(Props(router))
}
