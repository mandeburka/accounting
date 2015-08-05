package accounting.models

case class Account(id: Int, name: String, transactions: Vector[Transaction]) {
  val balance = ???
}
