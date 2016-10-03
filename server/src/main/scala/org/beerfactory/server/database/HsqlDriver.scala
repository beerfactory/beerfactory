package org.beerfactory.server.database

object HsqlDriver extends slick.driver.HsqldbDriver with BeerfactoryDriver {
  def engine: DBEngine = HsqldbEngine
}