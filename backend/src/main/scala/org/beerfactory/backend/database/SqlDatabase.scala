/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend.database

import com.typesafe.scalalogging.StrictLogging
import org.flywaydb.core.Flyway
import slick.driver.JdbcProfile
import slick.jdbc.JdbcBackend._
import DatabaseConfig._

import scala.util.{Failure, Success, Try}

case class SqlDatabase(
                        db: slick.jdbc.JdbcBackend.Database,
                        driver: JdbcProfile,
                        connectionString: JdbcConnectionString
                      )  {

  import driver.api._

  def updateSchema() {
    val flyway = new Flyway()
    flyway.setDataSource(connectionString.url, connectionString.username, connectionString.password)
    flyway.migrate()
  }

  def close() {
    db.close()
  }
}

case class JdbcConnectionString(url: String, username: String = "", password: String = "")


object SqlDatabase extends StrictLogging {
  def init(config: DatabaseConfig): Try[SqlDatabase] = {
    val db = config.engine match {
      case DatabaseConfig.h2Engine => initH2(config)
      case DatabaseConfig.pgEngine => initPg(config)
      case x => Failure(new IllegalArgumentException(s"Indetermined engine for datasource configuration '$x'"))
    }
    logger.debug("Database configuration: " + db.getOrElse("FAILURE"))
    if(db.isFailure)
      logger.debug(s"Cause: $db")
    db
  }

  private def initH2(config: DatabaseConfig): Try[SqlDatabase] = {
    try {
      val db = Database.forConfig(databaseConfigPath, config.hoconConfig)
      Success(SqlDatabase(db, slick.driver.H2Driver, JdbcConnectionString(config.dbURL)))
    }
    catch {
      case exc:Throwable => Failure(exc)
    }
  }

  private def initPg(config: DatabaseConfig): Try[SqlDatabase] = {
    try {
      val db = Database.forConfig(databaseConfigPath, config.hoconConfig)
      Success(SqlDatabase(db, slick.driver.PostgresDriver, JdbcConnectionString(config.dbURL)))
    }
    catch {
      case exc:Throwable => Failure(exc)
    }
  }
}