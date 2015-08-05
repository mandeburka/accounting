package accounting.actors

import akka.actor.{ReceiveTimeout, Actor}
import scala.concurrent.duration._

object AccountActor {

  sealed trait Message
  case class Credit(amount: Int) extends Message
  case class Debit(amount: Int) extends Message
  case object Summary extends Message

}

class AccountActor(id: Int) extends Actor {
  import AccountActor._
  val store = context.actorSelection("/accounting/user/store")
  context.setReceiveTimeout(1 minute)
  def receive: Receive = {
    case Debit(amount) => store.tell(StoreActor.Update(id, amount), sender())
    case Credit(amount) => store.tell(StoreActor.Update(id, -amount), sender())
    case Summary => store.tell(StoreActor.Get(id), sender())
    case ReceiveTimeout =>
      context.stop(self)
  }
}
