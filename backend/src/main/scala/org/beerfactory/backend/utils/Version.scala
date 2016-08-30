package org.beerfactory.backend.utils

/**
  * Created by njouanin on 30/08/16.
  */
object Version {
  val p = getClass.getPackage
  val name = p.getImplementationTitle
  val version = p.getImplementationVersion

  def prettyName = s"$name-$version"
}
