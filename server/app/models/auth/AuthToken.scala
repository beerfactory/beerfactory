/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package models.auth

import java.time.Instant

/**
  * A token to authenticate a user against an endpoint for a short time period.
  *
  * @param tokenId The unique token ID.
  * @param userId The user Id the token is associated with.
  * @param expiry The date-time the token expires.
  */
case class AuthToken(tokenId: String, userId: String, expiry: Instant)
