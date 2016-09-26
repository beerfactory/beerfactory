package org.beerfactory.backend.core

import java.util.UUID

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import akka.pattern.ask
import org.scalactic._

import scala.concurrent.duration._
import scala.util.Random

/**
  * Created by njouanin on 01/07/16.
  */
class CryptoActorSpec extends FlatSpec with Matchers with BeforeAndAfterAll with ScalaFutures with IntegrationPatience {
  implicit val timeout = Timeout(10 seconds)
  implicit val system = ActorSystem("TestActorSystem", ConfigFactory.load())

  override def afterAll {
    system.terminate()
  }

  "CryptoActor" should "hash password" in {
    val actorRef = TestActorRef(new CryptoActor())
    val hash = ask(actorRef, CryptoActor.HashPassword("password")).futureValue
    hash shouldBe a [String]
    hash.asInstanceOf[String].isEmpty shouldEqual false
  }

  it should "hash multiple passwords" in {
    val config = ConfigFactory.parseString("""initial-salt-queue-size: 1
                                             | min-salt-queue-size: 10
                                             | max-salt-queue-size: 1000
                                             | salt-size: 32
                                             | key-iteration-count: 10000
                                             | key-length: 256
                                             | """.stripMargin)
    val actorRef = TestActorRef(new CryptoActor())
    for(i <- 1 to 10)
    {
      val hash = ask(actorRef, CryptoActor.HashPassword(Random.nextString(10))).futureValue
      hash shouldBe a [String]
    }
    val stats = (ask(actorRef, CryptoActor.GetStats).futureValue).asInstanceOf[CryptoActorStat]
    stats.nbSalt shouldEqual 10
  }

  it should "pass checking correct password" in {
    import scala.concurrent.ExecutionContext.Implicits.global

    val password = "some secured password"
    val actorRef = TestActorRef(new CryptoActor())

    val fut = for {
      hash <- ask(actorRef, CryptoActor.HashPassword(password)).mapTo[String]
      test <- ask(actorRef, CryptoActor.CheckPassword(password, hash)).mapTo[Validation[ErrorMessage]]
    } yield test
    fut.futureValue shouldEqual Pass
  }

  it should "fail checking correct password" in {
    import scala.concurrent.ExecutionContext.Implicits.global

    val password = "some secured password"
    val actorRef = TestActorRef(new CryptoActor())

    val fut = for {
      hash <- ask(actorRef, CryptoActor.HashPassword(password)).mapTo[String]
      test <- ask(actorRef, CryptoActor.CheckPassword("wrong password", hash)).mapTo[Validation[ErrorMessage]]
    } yield test
    fut.futureValue shouldEqual Fail("checkPassword.password.noMatch")
  }
}
