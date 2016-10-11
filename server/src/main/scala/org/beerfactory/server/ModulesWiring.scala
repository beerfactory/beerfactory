package org.beerfactory.server

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.StrictLogging
import org.beerfactory.server.users.{UsersServiceConfig, UserDao, UsersService}
import org.beerfactory.server.core.{CryptoActor, UUIDActor}
import org.beerfactory.server.database.{DatabaseConfig, SqlDatabase}

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

  lazy val config = new DatabaseConfig with UsersServiceConfig with ServerConfig {
    override def hoconConfig = ConfigFactory.load()
  }

  lazy val uuidActor = system.actorOf(UUIDActor.props(), name = "uuidActor")
  lazy val cryptoActor = system.actorOf(CryptoActor.props(), name = "cryptoActor")

  lazy val daoExecutionContext = system.dispatchers.lookup("beerfactory.server.dao-dispatcher")
  lazy val serviceExecutionContext = system.dispatchers.lookup("beerfactory.server.service-dispatcher")

  lazy val sqlDatabase:SqlDatabase = SqlDatabase.init(config).get

  lazy val accountDao = new UserDao(sqlDatabase, uuidActor)(daoExecutionContext)

  lazy val usersService = new UsersService(config, accountDao, uuidActor, cryptoActor)(serviceExecutionContext)

}
