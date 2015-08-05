package accounting.actors

import akka.actor.{ReceiveTimeout, Actor}
import akka.event.Logging
import scala.concurrent.duration._

object AccountActor {

  sealed trait Message
  case class Credit(amount: Int) extends Message
  case class Debit(amount: Int) extends Message
  case object Summary extends Message

}

class AccountActor(id: Int) extends Actor {
  import AccountActor._
  val log = Logging(context.system, this)
  val store = context.actorSelection("/user/store")
  context.setReceiveTimeout(1 minute)
  def receive: Receive = {
    case Debit(amount) =>
      log.info(s"Trying to debit account ($id) with $amount")
      store.tell(StoreActor.Update(id, amount), sender())
    case Credit(amount) =>
      log.info(s"Trying to credit account ($id) with $amount")
      store.tell(StoreActor.Update(id, -amount), sender())
    case Summary => store.tell(StoreActor.Get(id), sender())
    case ReceiveTimeout =>
      context.stop(self)
  }
}
