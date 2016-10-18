package models

import java.time.ZonedDateTime

/**
  * A token to authenticate a user against an endpoint for a short time period.
  *
  * @param tokenId The unique token ID.
  * @param user The user the token is associated with.
  * @param expiry The date-time the token expires.
  */
case class AuthToken(tokenId: String, user: User, expiry: ZonedDateTime)
