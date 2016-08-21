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

import scala.util.{Failure, Success}

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
  def create(config: DatabaseConfig): SqlDatabase = {
    config.engine match {
      case Success(DatabaseConfig.h2Engine) => createH2(config)
      case Success(DatabaseConfig.pgEngine) => createPg(config)
      case Failure(exc) => throw exc
    }
  }

  private def createH2(config: DatabaseConfig): SqlDatabase = {
    val db = Database.forConfig(databaseConfigPath, config.hoconConfig)
    config.dbURL match {
      case Success(url: String) => SqlDatabase(db, slick.driver.H2Driver, JdbcConnectionString(url))
      case Failure(exc) => throw exc
    }
  }
  private def createPg(config: DatabaseConfig): SqlDatabase = {
    val db = Database.forConfig(databaseConfigPath, config.hoconConfig)
    config.dbURL match {
      case Success(url: String) => SqlDatabase(db, slick.driver.PostgresDriver, JdbcConnectionString(url))
      case Failure(exc) => throw exc
    }
  }
}