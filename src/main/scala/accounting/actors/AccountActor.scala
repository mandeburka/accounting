package accounting.actors

import akka.actor.Actor

object AccountActor {

  sealed trait Message
  case class Credit(amount: Int) extends Message
  case class Debit(amount: Int) extends Message
  case object Summary extends Message

}

class AccountActor(id: Int) extends Actor {

  def receive: Receive = ???
}
