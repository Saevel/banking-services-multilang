package prv.saevel.transactions.service

import scala.concurrent.duration.FiniteDuration

case class ApplicationConfig(parallelism: Int,
                             transactionsByIdTopic: String,
                             kafkaBootstrapServers: String,
                             kafkaGroupId: String,
                             transactionsReadTimeout: FiniteDuration)
