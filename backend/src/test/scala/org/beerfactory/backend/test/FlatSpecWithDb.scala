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

trait FlatSpecWithDb extends FlatSpec with Matchers with BeforeAndAfterAll with ScalaFutures
  with IntegrationPatience {

  private val connectionString = "jdbc:hsqldb:mem:berfactory_test" + this.getClass.getSimpleName
  val sqlDatabase = SqlDatabase.initEmbedded(connectionString, "SA", "").get

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
    sqlDatabase.dropSchema()
  }

  private def createAll() {
    sqlDatabase.updateSchema()
  }
}
