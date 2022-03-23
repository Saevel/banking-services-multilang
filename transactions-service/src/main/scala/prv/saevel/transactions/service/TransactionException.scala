package prv.saevel.transactions.service

case class TransactionException(errorCode: TransactionErrorCode.Value, message: String, cause: Throwable)
  extends RuntimeException(message, cause)
