package accounting.actors

import accounting.models.{Transaction, Account}
import akka.actor.Actor

object StoreActor {
  sealed trait Message
  case class Get(id: Int) extends Message
  case class Create(id: Int, name: String) extends Message
  case class Update(id: Int, transaction: Transaction) extends Message
}

class StoreActor extends Actor {
  import StoreActor._
  override def receive: Receive = withAccounts(Map.empty)
  def withAccounts(accounts: Map[Int, Account]): Actor.Receive = {
    case Get(id) => sender() ! accounts.get(id).map(Right(_)).getOrElse(Left(s"No such account ($id)"))
    case Create(id, name) => sender() ! accounts.get(id).map(_ => Left(s"Account ($id) already exist")).getOrElse {
      val account = Account(id, name, Vector.empty)
      context.become(withAccounts(accounts + (id -> account)))
      Right(account)
    }
    case Update(id, transaction) => sender() ! accounts.get(id).map {
      account =>
        val updated = account.updated(transaction)
        context.become(withAccounts(accounts + (id -> updated)))
        Right(updated)
    }.getOrElse(Left(s"No such account ($id)"))
  }
}
