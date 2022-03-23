package prv.saevel.transactions.service

case object TransactionErrorCode extends Enumeration {
  val IncorrectRequestEntityType, NonJsonBody, NotATransaction, UnableToSendToKafka = Value
}
