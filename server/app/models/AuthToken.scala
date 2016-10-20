package models

import java.time.Instant

/**
  * A token to authenticate a user against an endpoint for a short time period.
  *
  * @param tokenId The unique token ID.
  * @param userId The user Id the token is associated with.
  * @param expiry The date-time the token expires.
  */
case class AuthToken(tokenId: String, userId: String, expiry: Instant)
