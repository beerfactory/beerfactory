/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.server.utils

import java.util.concurrent.TimeUnit
import com.typesafe.config.{Config, ConfigException}
import scala.util.{Failure, Success, Try}

trait ConfigTry {
  def hoconConfig: Config

  def getBoolean(path: String) = getTry(path) { _.getBoolean(path) }
  def getString(path: String) = getTry(path) { _.getString(path) }
  def getInt(path: String) = getTry(path) { _.getInt(path) }
  def getConfig(path: String) = getTry(path) { _.getConfig(path) }
  def getSeconds(path: String) = getTry(path) { _.getDuration(path, TimeUnit.SECONDS) }
  def getMilliseconds(path: String) = getTry(path) { _.getDuration(path, TimeUnit.MILLISECONDS) }
  def getNanoseconds(path: String) = getTry(path) { _.getDuration(path, TimeUnit.NANOSECONDS) }

  protected def getTry[T](fullPath: String)(get: Config => T):Try[T] =
    getTry(hoconConfig, fullPath)(get)

  protected def getTry[T](baseConfig: Config, path: String)(get: Config => T):Try[T] = {
    try {
      Success(get(baseConfig))
    }
    catch {
      case exc:ConfigException => Failure(exc)
    }
  }
}