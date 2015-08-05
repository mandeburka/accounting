package accounting.models

object Transaction {
  object TransactionType extends Enumeration {
    type TransactionType = Value
    val Debit, Credit = Value
  }
}

case class Transaction(
    id: Int,
    amount: Int,
    transactionType: Transaction.TransactionType.Value)
