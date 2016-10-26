package controllers.auth.api

import java.time.Instant

import play.api.libs.json.{Format, Json}

/**
  * Created by njouanin on 26/10/16.
  */
case class Token(expiry: Instant)

object Token {
  implicit val format: Format[Token] = Json.format[Token]
}
