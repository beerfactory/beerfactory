package org.beerfactory.backend.database

object HsqlDriver extends slick.driver.HsqldbDriver with BeerfactoryDriver {
  def engine: DBEngine = HsqldbEngine
}