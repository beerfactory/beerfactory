/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.server.view

object Assets {
  val assetsDir = "/assets"

  def resource(relativePath: String): String = s"/resources/beerfactory/$relativePath"
  def webJar(webJar: String, partialPath: String): String = s"$assetsDir/$webJar/$partialPath"
}
