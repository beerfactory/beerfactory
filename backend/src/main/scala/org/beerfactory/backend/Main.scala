/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging

class Main extends StrictLogging {
  def start() = {
    implicit val _system = ActorSystem("main")
    implicit val _materializer = ActorMaterializer()
    import _system.dispatcher

    val modules = new ModulesWiring {
      lazy val system = _system
      println(sqlDatabase)
    }

  }
}

object Main extends App with StrictLogging {
  try {
    new Main().start()
  } catch {
    case exc:Throwable => {
      logger.error(s"Application startup failed: $exc")
      logger.debug("Stacktrace:", exc)
    }
  }
}
