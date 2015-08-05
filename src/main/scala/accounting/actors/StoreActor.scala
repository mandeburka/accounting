package accounting.actors

import accounting.models.{Transaction, Account}
import akka.actor.Actor
import akka.event.Logging
import math.abs

object StoreActor {
  sealed trait Message
  case class Get(id: Int) extends Message
  case class Create(id: Int, name: String) extends Message
  case class Update(id: Int, amount: Int) extends Message
}

class StoreActor extends Actor {
  import StoreActor._
  val log = Logging(context.system, this)
  override def receive: Receive = withAccounts(Map.empty)
  def withAccounts(accounts: Map[Int, Account], nextId: Int = 1): Actor.Receive = {
    case Get(id) => sender() ! accounts.get(id).map(Right(_)).getOrElse(Left(s"No such account ($id)"))
    case Create(id, name) => sender() ! accounts.get(id).map(_ => Left(s"Account ($id) already exist")).getOrElse {
      val account = Account(id, name, Vector.empty)
      context.become(withAccounts(accounts + (id -> account)))
      log.info(s"Created account ($id)")
      Right(account)
    }
    case Update(id, amount) => sender() ! accounts.get(id).map {
      account =>
        val transactionType = if (amount > 0) Transaction.TransactionType.Debit else Transaction.TransactionType.Credit
        val transaction = Transaction(id = nextId, amount = abs(amount), transactionType = transactionType)
        val updated = account.updated(transaction)
        if (updated.balance < 0) {
          log.info(s"Failed to updated account ($id) with $amount")
          Left(s"Cannot update account ($id) with $amount")
        } else {
          context.become(withAccounts(accounts + (id -> updated), nextId + 1))
          log.info(s"Updated account ($id). New balance ${updated.balance}")
          Right(updated)
        }
    }.getOrElse(Left(s"No such account ($id)"))
  }
}
