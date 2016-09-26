package org.beerfactory.backend

/**
  * Created by njouanin on 23/08/16.
  */
package object core {
  def timeNano[A](f: => A):(A, Long) = {
    val t0 = System.nanoTime
    (f, System.nanoTime-t0)
  }

  def timeMillis[A](f: => A):(A, Long) = {
    val t0 = System.currentTimeMillis
    (f, System.currentTimeMillis-t0)
  }
}