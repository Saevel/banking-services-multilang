package prv.saevel.transactions.service

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

case class PersistentTransaction(id: String,
                                 timestamp: String,
                                 sourceAccountId: Option[Long],
                                 targetAccountId: Option[Long],
                                 currency: String,
                                 amount: Double,
                                 transactionType: String)

object PersistentTransaction {

  def fromTransaction(transaction: Transaction)
                     (implicit formatter: DateTimeFormatter): PersistentTransaction = PersistentTransaction(
    UUID.randomUUID().toString,
    LocalDateTime.now().format(formatter),
    transaction.sourceAccountId,
    transaction.targetAccountId,
    transaction.currency,
    transaction.amount,
    transaction.transactionType
  )

  implicit val jsonFormat: RootJsonFormat[PersistentTransaction] = jsonFormat7(PersistentTransaction.apply _)
}