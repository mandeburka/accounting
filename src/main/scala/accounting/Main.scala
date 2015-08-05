package accounting

import accounting.actors.{StoreActor, Router}
import akka.actor._

object Main {
  def main(args: Array[String]) {
    val system = ActorSystem("accounting")
    system.actorOf(Props[StoreActor], "store")

    val router = system.actorOf(Props[Router], "router")
    system.actorOf(Props(classOf[Terminator], router), "terminator")

    router ! PoisonPill
  }
}

class Terminator(ref: ActorRef) extends Actor with ActorLogging {
  context watch ref
  def receive = {
    case Terminated(_) =>
      log.info("{} has terminated, shutting down system", ref.path)
      context.system.shutdown()
  }
}