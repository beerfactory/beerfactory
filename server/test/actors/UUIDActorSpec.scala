/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package actors

import play.api.Configuration
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

class UUIDActorSpec extends FlatSpec with Matchers with BeforeAndAfterAll with ScalaFutures with IntegrationPatience {
  implicit val timeout = Timeout(10 seconds)
  implicit val system = ActorSystem("TestActorSystem", ConfigFactory.load())

  override def afterAll {
    system.terminate().futureValue
  }

  "UUIDactor" should "generate UUIDs" in {
    val actorRef = TestActorRef(new UUIDActor(Configuration.empty))
    val result = ask(actorRef, UUIDActor.GetUUID).mapTo[String].futureValue
    result.isEmpty shouldBe false
  }
}
