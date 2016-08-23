package org.beerfactory.backend

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.StrictLogging
import org.beerfactory.backend.account.{AccountDao, AccountService}
import org.beerfactory.backend.core.{AccountConfig, CryptoActor, UUIDActor}
import org.beerfactory.backend.database.{DatabaseConfig, SqlDatabase}

/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
trait ModulesWiring extends StrictLogging {
  def system: ActorSystem

  lazy val config = new DatabaseConfig with AccountConfig {
    override def hoconConfig = ConfigFactory.load()
  }

  lazy val uuidActor = system.actorOf(UUIDActor.props(), name = "uuidActor")
  lazy val cryptoActor = system.actorOf(CryptoActor.props(), name = "cryptoActor")

  lazy val daoExecutionContext = system.dispatchers.lookup("beerfactory.server.dao-dispatcher")
  lazy val serviceExecutionContext = system.dispatchers.lookup("service-dispatcher")

  lazy val sqlDatabase:SqlDatabase = SqlDatabase.init(config).get

  lazy val accountDao = new AccountDao(sqlDatabase, uuidActor)(daoExecutionContext)

  lazy val accountService = new AccountService(config, accountDao, uuidActor, cryptoActor)(serviceExecutionContext)

}
