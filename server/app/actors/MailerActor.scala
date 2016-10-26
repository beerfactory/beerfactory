/*
 *********************************************************************************
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <nico@beerfactory.org> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Nicolas JOUANIN
 *********************************************************************************
 */
package actors

import javax.inject.Inject

import akka.actor.Actor
import play.api.libs.mailer.{Email, MailerClient}

object MailerActor {
  case class Send(email: Email)
}

class MailerActor @Inject()(mailerClient: MailerClient) extends Actor {
  import actors.MailerActor.Send

  def receive: Receive = {
    case Send(mail) ⇒ mailerClient.send(mail)
  }
}
