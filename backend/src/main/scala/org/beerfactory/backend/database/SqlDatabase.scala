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

import org.flywaydb.core.Flyway
import slick.driver.JdbcProfile

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
