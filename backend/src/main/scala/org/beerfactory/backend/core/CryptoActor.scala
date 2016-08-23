package org.beerfactory.backend.core

import java.security.SecureRandom
import javax.crypto.{SecretKey, SecretKeyFactory}
import javax.crypto.spec.PBEKeySpec

import akka.actor.{Actor, Props, Stash}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.StrictLogging
import org.beerfactory.backend.utils.Codecs
import org.scalactic.{ErrorMessage, Fail, Pass, Validation}

import scala.collection.mutable
import scala.concurrent.duration.Duration

object CryptoActor {
  def props(aConfig: Option[Config]=None): Props = Props(new CryptoActor(aConfig))
  case class HashPassword(password: String)
  case class CheckPassword(password: String, hash: String)
  case object GenerateSalt
  case object GetStats
}

case class CryptoActorStat(nbSalt: Long, startupTime: Long, nbRefill: Long, cumulRefillMilliTime: Long, cumulHashMilliTime: Long, nbHash: Long) {
  def nbSaltDAvg = (nbSalt * 1000) / (System.currentTimeMillis() - startupTime)
  def avgRefillTime = if(cumulRefillMilliTime > 0) cumulRefillMilliTime/ nbRefill else 0L
  def avgHashTime = if(cumulHashMilliTime > 0) cumulHashMilliTime / nbHash else 0L

  def prettyPrint = s"$nbSalt salt generated since since startup ($nbSaltDAvg salt per sec), $nbRefill refill since startup ($avgRefillTime ms per refill), " +
    s"$nbHash password hashed ($avgHashTime ms per hash)"
}

class CryptoActor(aConfig: Option[Config]=None) extends Actor with Stash with StrictLogging {
  import CryptoActor._
  import context.dispatcher

  type Salt = Array[Byte]

  val secureRandom = new SecureRandom()
  val keyFactory = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
  val saltQueue = mutable.Queue[Salt]()
  var stats:CryptoActorStat = _

  val defaultConfig = ConfigFactory.parseString("""initial-salt-queue-size: 1000
                                                  | min-salt-queue-size: 10
                                                  | max-salt-queue-size: 1000
                                                  | salt-size: 32
                                                  | key-iteration-count: 20000
                                                  | key-length: 256
                                                  | """.stripMargin)
  val actorConfig = aConfig match {
    case None => defaultConfig
    case Some(x:Config) => x.withFallback(defaultConfig)
  }

  val initSize = actorConfig.getInt("initial-salt-queue-size")
  val minSize = actorConfig.getInt("min-salt-queue-size")
  val maxSize = actorConfig.getInt("max-salt-queue-size")
  val saltSize = actorConfig.getInt("salt-size")
  val keyIterationCount = actorConfig.getInt("key-iteration-count")
  val keyLength = actorConfig.getInt("key-length")

  override def preStart(): Unit = {
    stats = CryptoActorStat(0, System.currentTimeMillis(), 0, 0, 0, 0)
    refill(initSize)
  }

  /**
    * Refill the salt buffer up to bufferSize salts
    * @param bufferSize targeted buffer size after refill
    */
  private def refill(bufferSize: Int) = {
    val (_, t) = timeMillis {
      for (i <- saltQueue.size to bufferSize) {
        val salt = new Array[Byte](saltSize)
        secureRandom.nextBytes(salt)
        saltQueue.enqueue(salt)
      }
    }
    stats = stats.copy(nbRefill = stats.nbRefill + 1, cumulRefillMilliTime = stats.cumulRefillMilliTime + t)
  }

  private def hash(password: String, salt: Array[Byte], specIteration: Int, specLength: Int): String = {
    val(res, t) = timeMillis {
      val spec = new PBEKeySpec( password.toCharArray, salt, specIteration, specLength )
      val key = keyFactory.generateSecret( spec ).getEncoded
      s"$keyIterationCount:${Codecs.base64Encode(salt)}:${Codecs.base64Encode(key)}"
    }
    stats = stats.copy(nbHash = stats.nbHash + 1, cumulHashMilliTime = stats.cumulHashMilliTime + t)
    if(t > 500) //500ms
      logger.warn(s"Password hash Time ${t}ms> 500ms")
    else
      logger.debug(s"Password hash Time: ${t}ms")

    res
  }

  private def checkPassword(testPassword: String, hashString: String): Validation[ErrorMessage] = {
    try {
      val Array(iterStr, saltStr, keyStr) = hashString.split(":")
      val iter = iterStr.toInt
      val salt = Codecs.base64Decode(saltStr)
      val key = Codecs.base64Decode(keyStr)

      val testHash = hash(testPassword, salt, iter, key.length * 8)
      if(hashString == testHash)
        Pass
      else
        Fail("checkPassword.password.noMatch")
    } catch {
      case e:Exception =>
        logger.warn(s"Exception caught will checking password: password not shown, hashString=$hashString", e)
        Fail("checkPassword.exceptionCaught")
    }
  }

  private def getNextSalt() = {
    stats = stats.copy(nbSalt = stats.nbSalt + 1)
    val salt = saltQueue.dequeue()
    salt
  }

  def receive: Receive = {
    case GetStats => sender() ! stats
    case CheckPassword(password, hash) => sender() ! checkPassword(password, hash)
    case HashPassword(password) =>
      sender() ! hash(password, getNextSalt(), keyIterationCount, keyLength)
      if(saltQueue.size < minSize)
      {
        context.become({
          case GenerateSalt ⇒
            refill(maxSize)
            logger.debug(s"Salt queue refilled.")
            context.unbecome()
            unstashAll()
          case _ => stash()
        })
        self ! GenerateSalt
      }
  }
}