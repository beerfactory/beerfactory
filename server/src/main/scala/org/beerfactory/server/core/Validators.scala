/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.server.core

import org.scalactic.{Fail, Pass, Validation}

object Validators {

  def validate(errorCode:String="validation failed", predicate: => Boolean): Validation[String] =
    if(predicate) Pass else Fail(errorCode)

  def notEmpty(errorCode: String)(validated: String) = validate(errorCode, !validated.isEmpty)
  def notBlank(errorCode:String)(validated: String) = notEmpty(errorCode)(validated.trim)
  def minSize(errorCode: String, minSize: Int)(validated: String) = validate(errorCode, validated.length >= minSize)
  def maxSize(errorCode: String, maxSize: Int)(validated: String) = validate(errorCode, validated.length <= maxSize)

  def validEmailAddress(errorCode: String)(validated: String) = {
    def isValidEmail(str: String): Boolean = {
      val emailRegex = """(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])""".r
      emailRegex.findFirstIn(str) match {
        case Some(_) => true
        case None => false
      }
    }
    validate(errorCode, isValidEmail(validated))
  }
}