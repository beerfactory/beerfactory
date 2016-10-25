/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package controllers.api

import play.api.libs.json.{Format, Json}

sealed trait Response {
  def status: String
}

case class Good() extends Response {
  val status = "OK"
}
object Good {
  implicit val format: Format[Good] = Json.format[Good]
}

case class Bad(errors: Seq[String]) extends Response {
  val status = "KO"
}
object Bad {
  implicit val format: Format[Bad] = Json.format[Bad]

  def apply(error: String) = new Bad(Seq(error))
}

