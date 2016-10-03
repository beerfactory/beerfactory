package org.beerfactory.server.version

/**
  * Created by njouanin on 30/08/16.
  */
object Version {
  val p = getClass.getPackage
  val name = BuildInfo.projectName + "-" + BuildInfo.name
  val version = BuildInfo.version
  val buildTime = BuildInfo.builtAtString

  def prettyName = s"$name-$version"
}
