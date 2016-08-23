package org.beerfactory.backend.database

import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging
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
trait DatabaseConfig extends ConfigTry with StrictLogging {
  import DatabaseConfig._
  def hoconConfig: Config

  lazy val engine = getString(dataSourceClassPath) match {
    case Success(dsClass:String) => {
      if(dsClass.toUpperCase.contains("HSQL"))
        hsqlEngine
      else
      {
        if(dsClass.toUpperCase.contains("PG"))
          pgEngine
        else
        {
          logger.warn(s"Can't determine Database engine for datasourceClass: '$dsClass'")
          dsClass
        }
      }
    }
    case Failure(_) => {
      logger.warn(s"Can't read parameter $dataSourceClassPath value, falling back to HSQL")
      hsqlEngine
    }

  }

  lazy val dbURL = engine match {
    case `hsqlEngine` => getString(databaseConfigPath + ".properties.url").get
    case `pgEngine` => {
      val host = getString(databaseConfigPath + ".properties.serverName").get
      val port = getString(databaseConfigPath + ".properties.portNumber").get
      val dbName = getString(databaseConfigPath + ".properties.databaseName").get
      s"jdbc:postgresql://$host:$port/$dbName"
    }
  }
}

object DatabaseConfig {
  val databaseConfigPath = "beerfactory.server.database"
  val dataSourceClassPath = databaseConfigPath + ".dataSourceClass"
  val hsqlEngine = "HSQL"
  val pgEngine = "PG"
}