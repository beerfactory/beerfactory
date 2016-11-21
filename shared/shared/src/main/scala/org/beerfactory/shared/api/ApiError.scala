/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package org.beerfactory.shared.api

case class ApiError(id: String, details: Seq[String], statusCode: Int)

object ApiError {
  def apply(id: String, statusCode: Int) =
    new ApiError(id, Seq.empty, statusCode)

  def apply(id: String, detail: String, statusCode: Int = 0) =
    new ApiError(id, Seq(detail), statusCode)

}
