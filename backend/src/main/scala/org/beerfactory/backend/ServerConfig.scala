/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend

import java.time.Duration

import com.typesafe.config.Config

trait ServerConfig {
  def hoconConfig: Config

  lazy val serverHost: String = hoconConfig.getString("beerfactory.server.host")
  lazy val serverPort: Int = hoconConfig.getInt("beerfactory.server.port")
  lazy val jwtAlgorithmName: String = hoconConfig.getString("beerfactory.server.jwt.algorithm")
  lazy val jwtTTL:Duration = hoconConfig.getDuration("beerfactory.server.jwt.ttl")
  lazy val jwtSecretKey: String = hoconConfig.getString("beerfactory.server.jwt.secret-key")
}