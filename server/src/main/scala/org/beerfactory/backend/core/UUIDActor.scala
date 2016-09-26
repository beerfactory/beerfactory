package org.beerfactory.backend.core

import java.util.UUID

import akka.actor.{Actor, Props}
import akka.actor.Stash
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.StrictLogging

import scala.collection.mutable

/**
  * Created by njouanin on 23/08/16.
  */
object UUIDActor {
  def props(aConfig: Option[Config]=None): Props = Props(new UUIDActor(aConfig))
  case object GenerateUUID
  case object GetUUID
  case object GetStats
}

/**
  * Case class containing UUIDActor stats
  * @param nbUUID number of UUID returned so far
  * @param startupTime actor startup time (milliseconds)
  * @param nbRefill number of UUID queue refills
  * @param cumulRefillMilliTime cumulative refill time so far (milliseconds)
  */
case class UUIDActorStat(nbUUID: Long, startupTime: Long, nbRefill: Long, cumulRefillMilliTime: Long) {
  def nbUUIDAvg = (nbUUID * 1000) / (System.currentTimeMillis() - startupTime)
  def avgRefillTime = cumulRefillMilliTime / nbRefill

  def prettyPrint = s"$nbUUID UUID served since since startup ($nbUUIDAvg UUID per sec), $nbRefill refill since startup ($avgRefillTime ms per refill)"
}

class UUIDActor(aConfig: Option[Config]=None) extends Actor with Stash with StrictLogging {
  import UUIDActor._

  val uuidQueue = mutable.Queue[UUID]()
  var stats:UUIDActorStat = _

  val defaultConfig = ConfigFactory.parseString("""initial-queue-size: 1000
                                                  | min-queue-size: 10
                                                  | max-queue-size: 1000
                                                  | """.stripMargin)
  val actorConfig = aConfig match {
    case None => defaultConfig
    case Some(x:Config) => x.withFallback(defaultConfig)
  }

  val initSize = actorConfig.getInt("initial-queue-size")
  val minSize = actorConfig.getInt("min-queue-size")
  val maxSize = actorConfig.getInt("max-queue-size")
  logger.debug(s"""Actor configuration: UUID queue size($initSize, $minSize, $maxSize)""")

  override def preStart(): Unit = {
    stats = UUIDActorStat(0, System.currentTimeMillis(), 0, 0)
    refill(initSize)
  }

  /**
    * Refill the UUID queue up to queueSize UUIDs
    * @param queueSize targeted queue size after refill
    */
  private def refill(queueSize: Int) = {
    val (_, t) = timeMillis {
      for (i <- uuidQueue.size to queueSize) uuidQueue.enqueue(UUID.randomUUID())
    }
    stats = stats.copy(nbRefill = stats.nbRefill + 1, cumulRefillMilliTime = stats.cumulRefillMilliTime + t)
  }

  /**
    * Return an UUID dequeued from the queue
    * @return an UUID
    */
  private def getNextUUID(): UUID = {
    stats = stats.copy(nbUUID = stats.nbUUID + 1)
    uuidQueue.dequeue()
  }

  def receive: Receive = {
    case GetStats => sender() ! stats
    case GetUUID ⇒
      sender() ! getNextUUID()
      logger.debug(s"UUID request served.")
      if(uuidQueue.size < minSize)
      {
        context.become({
          case GenerateUUID ⇒
            refill(maxSize)
            logger.debug(s"UUID queue refilled.")
            context.unbecome()
            unstashAll()
          case _ => stash()
        })
        self ! GenerateUUID
      }
  }
}
