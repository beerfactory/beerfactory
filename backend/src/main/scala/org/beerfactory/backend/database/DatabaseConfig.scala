package org.beerfactory.backend.database

import com.typesafe.config.Config
import org.beerfactory.backend.utils.ConfigTry

import scala.util.{Failure, Success}

/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
trait DatabaseConfig extends ConfigTry {
  import DatabaseConfig._
  def hoconConfig: Config

  lazy val engine = getString(dataSourceClassPath) match {
    case Success(dsClass:String) => {
      val h2Pattern = "H2".r
      val pgPattern = "PG".r
      dsClass.toUpperCase match {
        case h2Pattern() => h2Engine
        case pgPattern() => pgEngine
        case _ => Failure(new IllegalArgumentException(s"Unknown engine for datasourceClass: $dsClass"))
      }
    }
  }
}

object DatabaseConfig {
  val dataSourceClassPath = "beerfactory.server.database.dataSourceClass"
  val h2Engine = "H2"
  val pgEngine = "PG"
}