package controllers.api.auth

import java.time.Instant

import play.api.libs.json.{Format, Json}

/**
  * Created by njouanin on 26/10/16.
  */
case class Token(tokenId: String, expiry: Instant, email: String)

object Token {
  implicit val format: Format[Token] = Json.format[Token]
}
