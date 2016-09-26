/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.backend.core

import org.scalactic.{Bad, Good, One}
import org.scalatest.{FlatSpec, Matchers}
import org.scalactic.Accumulation._

class ValidatorsSpec extends FlatSpec with Matchers {
  import Validators._

  "notEmpty validator" should "validate non empty String" in {
    val result = Good("Test String") when notEmpty("testErrorCode")
    result shouldEqual Good("Test String")
  }

  it should "invalidate empty String" in {
    val result = Good("") when notEmpty("testErrorCode")
    result shouldEqual Bad(One("testErrorCode"))
  }

  "notBlank validator" should "validate non empty String" in {
    val result = Good("Test String") when notBlank("testErrorCode")
    result shouldEqual Good("Test String")
  }

  it should "invalidate empty String" in {
    val result = Good("   ") when notBlank("testErrorCode")
    result shouldEqual Bad(One("testErrorCode"))
  }

  "minSize validator" should "validate test String" in {
    val result = Good("0123456789") when minSize("testErrorCode", 10)
    result shouldEqual Good("0123456789")
  }

  it should "invalidate test String" in {
    val result = Good("0123456") when minSize("testErrorCode", 10)
    result shouldEqual Bad(One("testErrorCode"))
  }

  "maxSize validator" should "validate test String" in {
    val result = Good("0123456789") when minSize("testErrorCode", 10)
    result shouldEqual Good("0123456789")
  }

  it should "invalidate test String" in {
    val result = Good("012345678910") when maxSize("testErrorCode", 10)
    result shouldEqual Bad(One("testErrorCode"))
  }

  "validEmailAddress validator" should "validate address some@sample.com" in {
    val result = Good("some@sample.com") when validEmailAddress("testErrorCode")
    result shouldEqual Good("some@sample.com")
  }

  it should "invalidate some#invalid.com" in {
    val result = Good("some#invalid.com") when validEmailAddress("testErrorCode")
    result shouldEqual Bad(One("testErrorCode"))
  }
}