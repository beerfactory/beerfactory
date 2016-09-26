/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend.users

import com.typesafe.config.Config

trait UsersServiceConfig {
  def hoconConfig: Config

  private lazy val accountConfig = hoconConfig.getConfig("beerfactory.server.accountService")

  lazy val actorWaitTimeout = accountConfig.getDuration("actor-wait-timeout")
  lazy val loginMinSize = accountConfig.getInt("login-min-size")
  lazy val loginMaxSize = accountConfig.getInt("login-max-size")
  lazy val passwordMinSize = accountConfig.getInt("password-min-size")
  lazy val passwordMaxSize = accountConfig.getInt("password-max-size")
}
