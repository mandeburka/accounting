package accounting

import accounting.actors.{AccountActor, StoreActor, Router}
import akka.actor._

object Main {
  def main(args: Array[String]) {
    val system = ActorSystem("accounting")

    val store = system.actorOf(Props[StoreActor], "store")
    val router = system.actorOf(Props[Router], "router")
    system.actorOf(Props(classOf[Terminator], router), "terminator")

    store ! StoreActor.Create(1, "TestAccount")
    router ! Router.Forward(1, AccountActor.Debit(20))
    router ! Router.Forward(1, AccountActor.Credit(10))
    router ! Router.Forward(1, AccountActor.Credit(20))
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