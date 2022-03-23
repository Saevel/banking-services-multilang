package prv.saevel.transactions.service

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class Transaction(sourceAccountId: Option[Long],
                       targetAccountId: Option[Long],
                       currency: String,
                       amount: Double,
                       transactionType: String)

object Transaction extends DefaultJsonProtocol {
  implicit val jsonProtocol: RootJsonFormat[Transaction] = jsonFormat5(Transaction.apply _)
}