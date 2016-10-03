/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging
import scala.util.{Failure, Success}

class Main extends StrictLogging {
  def start() = {
    implicit val _system = ActorSystem("main")
    implicit val _materializer = ActorMaterializer()
    import _system.dispatcher

    val modules = new ModulesWiring with Routes {
      lazy val system = _system

    }
    modules.sqlDatabase.updateSchema()

    (Http().bindAndHandle(modules.routes, modules.config.serverHost, modules.config.serverPort), modules)

  }
}

object Main extends App with StrictLogging {
  try {
    val (startFuture, bl) = new Main().start()

    val host = bl.config.serverHost
    val port = bl.config.serverPort

    val system = bl.system
    import system.dispatcher

    startFuture.onComplete {
      case Success(b) =>
        logger.info(s"Server started on $host:$port")
        sys.addShutdownHook {
          b.unbind()
          bl.system.terminate()
          logger.info("Server stopped")
        }
      case Failure(e) =>
        logger.error(s"Cannot start server on $host:$port", e)
        sys.addShutdownHook {
          bl.system.terminate()
          logger.info("Server stopped")
        }
    }

  } catch {
    case exc:Throwable => {
      logger.error(s"Application startup failed: $exc")
      logger.debug("Stacktrace:", exc)
    }
  }
}
