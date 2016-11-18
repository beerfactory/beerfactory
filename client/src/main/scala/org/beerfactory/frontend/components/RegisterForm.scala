package org.beerfactory.frontend.components

import japgolly.scalajs.react.{BackendScope, ReactComponentB}
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.all._
import org.beerfactory.frontend.pages.Page

/**
  * Created by njouanin on 18/11/16.
  */
object RegisterForm {
  case class Props(router: RouterCtl[Page])

  case class State()

  class Backend(scope: BackendScope[Props, State]) {

    def render(props: Props, state: State) =
      div(cls := "column", form(cls := "ui column large form attached segment"))
  }

  private val component =
    ReactComponentB[Props]("RegisterForm").initialState(State()).renderBackend[Backend].build

  def apply(props: Props) = component(props)
}
