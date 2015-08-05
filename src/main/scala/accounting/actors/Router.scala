package accounting.actors

import akka.actor._
import scala.concurrent.duration._

object Router {

  sealed trait Message
  case class Forward(id: Int, m: AccountActor.Message) extends Message
}

class Router extends Actor {
  context.setReceiveTimeout(10 second)
  def receive: Receive = withActors()
  def withActors(actors: Map[Int, ActorRef] = Map.empty): Actor.Receive = {
    case Router.Forward(id, m) =>
      actors.getOrElse(id, {
        val actor = context.actorOf(Props(classOf[AccountActor], id))
        context.become(withActors(actors + (id -> actor)))
        context watch actor
        actor
      }).tell(m, sender())
    case Terminated(a) =>
      context.become(withActors(actors.filterNot(_ == a)))
    case ReceiveTimeout =>
      context.stop(self)
  }
}
