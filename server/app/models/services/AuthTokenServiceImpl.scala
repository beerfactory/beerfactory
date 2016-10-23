/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package models.services
import java.time.Instant
import javax.inject.{Inject, Named}

import actors.UUIDActor.GetUUID
import akka.actor.ActorRef
import models.AuthToken
import models.daos.AuthTokenDao

import scala.concurrent.Future
import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import play.api.libs.concurrent.Execution.Implicits._



class AuthTokenServiceImpl @Inject()(@Named("uuidActor") uuidActor: ActorRef, authTokenDao: AuthTokenDao) extends AuthTokenService {
  implicit val timeout = Timeout(5 seconds)

  /**
    * Creates a new auth token and saves it in the backing store.
    *
    * @param userId The user Id for which the token should be created.
    * @param expiry The duration a token expires.
    * @return The saved auth token.
    */
  override def create(userId: String, expiry: FiniteDuration): Future[AuthToken] = {
    for {
      uid ← ask(uuidActor, GetUUID).mapTo[String]
      token <- authTokenDao.save(AuthToken(uid, userId, Instant.now().plusSeconds(expiry.toSeconds)))
    } yield AuthToken(token.tokenId, token.userId, token.expiry)
  }

  /**
    * Validates a token ID.
    *
    * @param id The token ID to validate.
    * @return The token if it's valid, None otherwise.
    */
  override def validate(id: String): Future[Option[AuthToken]] = {
    authTokenDao.find(id).flatMap {
      case Some(token) ⇒ Future.successful { if(Instant.now.isBefore(token.expiry)) Some(token) else None }
    }
  }


  /**
    * Cleans expired tokens.
    *
    * @return The list of deleted tokens.
    */
  override def clean: Future[Seq[AuthToken]] = ???
}
