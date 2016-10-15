/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package actors

import java.nio.ByteBuffer
import java.util.UUID
import javax.inject.Inject

import akka.actor.{Actor, Props, Stash}
import play.api.{Configuration, Logger}
import utils.Codecs

import scala.collection.mutable

object UUIDActor {
case object GenerateUUID
case object GetUUID
}

class UUIDActor @Inject()(config: Configuration) extends Actor with Stash {
  import UUIDActor._

  val logger: Logger = Logger(this.getClass())

  val uuidQueue = mutable.Queue[String]()

  val initSize = config.getInt("uuidactor.queue.size.init").getOrElse(1000)
  val minSize = config.getInt("uuidactor.queue.size.min").getOrElse(10)
  val maxSize = config.getInt("uuidactor.queue.size.max").getOrElse(1000)

  override def preStart(): Unit = {
    logger.debug(s"""Actor configuration: UUID queue size($initSize, $minSize, $maxSize)""")
    refill(initSize)
  }

  /**
    * Refill the UUID queue up to queueSize UUIDs
    * @param queueSize targeted queue size after refill
    */
  private def refill(queueSize: Int) = {
    for (i <- uuidQueue.size to queueSize) uuidQueue.enqueue(Codecs.toBase64(UUID.randomUUID))
  }

  /**
    * Return an UUID dequeued from the queue
    * @return an UUID
    */
  private def getNextUUID(): String = {
    uuidQueue.dequeue()
  }

  def receive: Receive = {
    case GetUUID ⇒
      sender() ! getNextUUID()
      logger.debug("UUID request served.")
      if(uuidQueue.size < minSize)
      {
        logger.debug("queue reaching low watermark.")
        context.become({
          case GenerateUUID ⇒
            refill(maxSize)
            logger.debug("UUID queue refilled.")
            context.unbecome()
            unstashAll()
          case _ => stash()
        })
        self ! GenerateUUID
      }
  }
}