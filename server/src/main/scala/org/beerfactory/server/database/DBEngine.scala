/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.server.database

import slick.driver.JdbcDriver

trait DBEngine
object PostgresqlEngine extends DBEngine
object HsqldbEngine extends DBEngine
case class OtherEngine(name: String) extends DBEngine

trait BeerfactoryDriver {
  def engine: DBEngine
}