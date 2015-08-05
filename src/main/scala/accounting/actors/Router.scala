package accounting.actors

import akka.actor.Actor

object Router {

  sealed trait Message
  case class Forward(id: Int, m: AccountActor.Message) extends Message
}

class Router extends Actor {

  def receive: Receive = ???
}
