package accounting.models

case class Account(id: Int, name: String, transactions: Vector[Transaction]) {
  val balance = transactions.foldLeft(0) {
    (acc, tr) =>
      val amount: Int = tr.transactionType match {
        case Transaction.TransactionType.Debit => tr.amount
        case Transaction.TransactionType.Credit => -tr.amount
      }
      amount + acc
  }

  def updated(t: Transaction): Account = Account(id, name, transactions :+ t)
}
