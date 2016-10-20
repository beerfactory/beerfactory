/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package models.services

import models.AuthToken

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration._

trait AuthTokenService {
  /**
    * Creates a new auth token and saves it in the backing store.
    *
    * @param userId The user Id for which the token should be created.
    * @param expiry The duration a token expires.
    * @return The saved auth token.
    */
  def create(userId: String, expiry: FiniteDuration = 5 minutes): Future[AuthToken]

  /**
    * Validates a token ID.
    *
    * @param id The token ID to validate.
    * @return The token if it's valid, None otherwise.
    */
  def validate(id: String): Future[Option[AuthToken]]

  /**
    * Cleans expired tokens.
    *
    * @return The list of deleted tokens.
    */
  def clean: Future[Seq[AuthToken]]
}
