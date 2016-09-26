package org.beerfactory.backend.core

import java.util.UUID

import akka.actor.ActorSystem
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.duration._
import akka.pattern.ask

import scala.concurrent.Await
import akka.testkit.TestActorRef
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

/**
  * Created by nico on 12/06/2016.
  */
class UUIDActorSpec extends FlatSpec with Matchers with BeforeAndAfterAll with ScalaFutures with IntegrationPatience {
  implicit val timeout = Timeout(10 seconds)
  implicit val system = ActorSystem("TestActorSystem", ConfigFactory.load())

  override def afterAll {
    system.terminate()
  }

  "CommonTools actor" should "generate UUIDs" in {
    val actorRef = TestActorRef(new UUIDActor)
    val result = ask(actorRef, UUIDActor.GetUUID).futureValue
    result shouldBe a [UUID]
  }

  "CommonTools actor" should "generate multiple UUIDs" in {
    val config = ConfigFactory.parseString("""initial-queue-size: 1
                                             | min-queue-size: 1
                                             | max-queue-size: 2
                                             | """.stripMargin)
    val actorRef = TestActorRef(new UUIDActor(Some(config)))
    for(i <- 1 to 10)
    {
      val result = ask(actorRef, UUIDActor.GetUUID).futureValue
      result shouldBe a [UUID]
    }
    val stats = (ask(actorRef, UUIDActor.GetStats).futureValue).asInstanceOf[UUIDActorStat]
    stats.nbUUID shouldEqual 10
  }
}