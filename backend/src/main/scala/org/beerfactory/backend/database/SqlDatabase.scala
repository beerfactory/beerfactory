/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend.database

import java.time.{OffsetDateTime, ZoneOffset}
import java.util.Properties

import com.typesafe.scalalogging.StrictLogging
import slick.driver.JdbcProfile
import slick.jdbc.JdbcBackend._
import DatabaseConfig._
import liquibase.Contexts
import java.sql.DriverManager
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor

import scala.util.{Failure, Success, Try}

case class SqlDatabase( db: slick.jdbc.JdbcBackend.Database,
                        driver: JdbcProfile with BeerfactoryDriver,
                        connectionString: JdbcConnectionString
                      )  {
  import driver.api._

  implicit val offsetDateTimeColumnType = MappedColumnType.base[OffsetDateTime, java.sql.Timestamp](
    dt => new java.sql.Timestamp(dt.toInstant.toEpochMilli),
    t => t.toInstant.atOffset(ZoneOffset.UTC)
  )

  private def liquiConnect() = {
    val conn = DriverManager.getConnection(connectionString.url, connectionString.username, connectionString.password)
    val database = DatabaseFactory.getInstance()
      .findCorrectDatabaseImplementation(new JdbcConnection(conn))
    new Liquibase("db/changelogs/changelog-master.xml", new ClassLoaderResourceAccessor(), database)

  }
  def updateSchema() {
    val conn = DriverManager.getConnection(connectionString.url, connectionString.username, connectionString.password)
    val database = DatabaseFactory.getInstance()
      .findCorrectDatabaseImplementation(new JdbcConnection(conn))
    val liquibase = new Liquibase("db/changelogs/changelog-master.xml", new ClassLoaderResourceAccessor(), database)
    liquibase.update(new Contexts())
    database.close()
  }

  def dropSchema() {
    liquiConnect().dropAll()
  }

  def close() {
    db.close()
  }
}

case class JdbcConnectionString(url: String, username: String = "", password: String = "")


object SqlDatabase extends StrictLogging {
  def init(config: DatabaseConfig): Try[SqlDatabase] = {
    val db = config.engine match {
      case HsqldbEngine => initHsql(config)
      case PostgresqlEngine => initPg(config)
      case x => Failure(new IllegalArgumentException(s"Indetermined engine for datasource configuration '$x'"))
    }
    logger.debug("Database configuration: " + db.getOrElse("FAILURE"))
    if(db.isFailure)
      logger.debug(s"Cause: $db")
    db
  }

  private def initHsql(config: DatabaseConfig): Try[SqlDatabase] = {
    try {
      val db = Database.forConfig(databaseConfigPath, config.hoconConfig)
      Success(SqlDatabase(db, HsqlDriver, JdbcConnectionString(config.dbURL)))
    }
    catch {
      case exc:Throwable => Failure(exc)
    }
  }

  private def initPg(config: DatabaseConfig): Try[SqlDatabase] = {
    try {
      val db = Database.forConfig(databaseConfigPath, config.hoconConfig)
      Success(SqlDatabase(db, PgDriver, JdbcConnectionString(config.dbURL)))
    }
    catch {
      case exc:Throwable => Failure(exc)
    }
  }

  def initFromConnection(driver: JdbcProfile with BeerfactoryDriver, connectionString: String, user: String, password: String): Try[SqlDatabase] = {
    try {
      val db = Database.forURL(connectionString, user, password)
      Success(SqlDatabase(db, driver, JdbcConnectionString(connectionString, user, password)))
    }
    catch {
      case exc:Throwable => Failure(exc)
    }
  }
}