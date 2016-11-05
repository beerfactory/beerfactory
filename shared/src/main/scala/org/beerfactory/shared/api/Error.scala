/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.shared.api

case class Error(id: String, details: Seq[String], statusCode: Int)

object Error {
  def apply(id: String, statusCode: Int) =
    new Error(id, Seq.empty, statusCode)

  def apply(id: String, detail: String, statusCode: Int = 0) =
    new Error(id, Seq(detail), statusCode)

}
