package models.daos

import java.time.ZonedDateTime
import javax.inject.{Inject, Named}

import akka.actor.ActorRef
import models.AuthToken
import models.daos.db.{DBAuthToken, DBAuthTokenSchema}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Created by njouanin on 18/10/16.
  */
trait AuthTokenDao {
  /**
    * Finds a token by its ID.
    *
    * @param id The unique token ID.
    * @return The found token or None if no token for the given ID could be found.
    */
  def find(id: String): Future[Option[AuthToken]]

  /**
    * Finds expired tokens.
    *
    * @param dateTime The current date time.
    */
  def findExpired(dateTime: ZonedDateTime): Future[Seq[AuthToken]]

  /**
    * Saves a token.
    *
    * @param token The token to save.
    * @return The saved token.
    */
  def save(token: AuthToken): Future[AuthToken]

  /**
    * Removes the token for the given ID.
    *
    * @param id The ID for which the token should be removed.
    * @return A future to wait for the process to be completed.
    */
  def remove(id: String): Future[Unit]
}

class AuthTokenDaoImpl @Inject()(
                                  @Named("uuidActor") configuredActor: ActorRef,
                                  protected val dbConfigProvider: DatabaseConfigProvider)
  extends AuthTokenDao with HasDatabaseConfigProvider[JdbcProfile] with DBAuthTokenSchema  {

  import driver.api._

  /**
    * Finds a token by its ID.
    *
    * @param id The unique token ID.
    * @return The found token or None if no token for the given ID could be found.
    */
  override def find(id: String): Future[Option[AuthToken]] = {
    db.run(DBAuthTokens.filter(_.tokenId === id).result.headOption).map {
      case Some(dbAuthToken:DBAuthToken) ⇒ Some(AuthToken(dbAuthToken.tokenId, dbAuthToken.userId, dbAuthToken.expiry))
    }
  }

  /**
    * Finds expired tokens.
    *
    * @param dateTime The current date time.
    */
  override def findExpired(dateTime: ZonedDateTime): Future[Seq[AuthToken]] = ???

  /**
    * Saves a token.
    *
    * @param token The token to save.
    * @return The saved token.
    */
  override def save(token: AuthToken): Future[AuthToken] = {
    db.run( DBAuthTokens += DBAuthToken(token.tokenId, token.userId, token.expiry))
      .map(_ ⇒ token)
  }

  /**
    * Removes the token for the given ID.
    *
    * @param id The ID for which the token should be removed.
    * @return A future to wait for the process to be completed.
    */
  override def remove(id: String): Future[Unit] = ???
}