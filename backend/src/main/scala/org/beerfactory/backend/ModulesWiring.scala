package org.beerfactory.backend

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.StrictLogging
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

  lazy val config = new DatabaseConfig {
    override def hoconConfig = ConfigFactory.load()
  }

  lazy val sqlDatabase:SqlDatabase = SqlDatabase.init(config).get


}
