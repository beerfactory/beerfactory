/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend.test

import org.beerfactory.backend.database._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}

trait FlatSpecWithDb extends FlatSpec with Matchers with BeforeAndAfterAll with BeforeAndAfterEach with ScalaFutures
  with IntegrationPatience {

  Class.forName("org.hsqldb.jdbcDriver") //Needed for hsqldb ?
  private val connectionString = "jdbc:hsqldb:mem:berfactory_test" + this.getClass.getSimpleName
  val sqlDatabase = SqlDatabase.initFromConnection(HsqlDriver, connectionString, "SA", "").get
  //private val connectionString = "jdbc:postgresql://localhost:5432/beerfactory"
  //val sqlDatabase = SqlDatabase.initFromConnection(PgDriver, connectionString, "beerfactory", "beerfactory").get


  override protected def beforeAll() {
    super.beforeAll()
    createAll()
  }

  override protected def beforeEach() {
    super.beforeEach()
    //dropAll()
    //createAll()
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
