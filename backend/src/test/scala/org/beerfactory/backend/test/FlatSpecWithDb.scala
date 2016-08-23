/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend.test

import org.beerfactory.backend.database.SqlDatabase
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}

trait FlatSpecWithDb extends FlatSpec with Matchers with BeforeAndAfterAll with BeforeAndAfterEach with ScalaFutures
  with IntegrationPatience {

  private val connectionString = "jdbc:hsqldb:mem:berfactory_test" + this.getClass.getSimpleName
  val sqlDatabase = SqlDatabase.initEmbedded(connectionString).get

  override protected def beforeAll() {
    super.beforeAll()
    createAll()
  }

  def clearData() {
    dropAll()
    createAll()
  }

  override protected def afterAll() {
    super.afterAll()
    dropAll()
    sqlDatabase.close()
  }

  private def dropAll() {
    import sqlDatabase.driver.api._
    sqlDatabase.db.run(sqlu"DROP ALL OBJECTS").futureValue
  }

  private def createAll() {
    sqlDatabase.updateSchema()
  }

  override protected def afterEach() {
    try {
      clearData()
    }
    catch {
      case e: Exception => e.printStackTrace()
    }

    super.afterEach()
  }
}
