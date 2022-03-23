package prv.saevel.transactions.service

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.{PredefinedFromEntityUnmarshallers, Unmarshal}
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.Materializer
import akka.stream.scaladsl.{Keep, Sink}
import com.spikhalskiy.futurity.Futurity
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import prv.saevel.transactions.service.TransactionErrorCode._
import spray.json.{JsValue, JsonParser, enrichAny}

import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

object TransactionStreams extends PredefinedFromEntityUnmarshallers {

  import scala.jdk.javaapi.FutureConverters.asScala

  def persistTransactionById(request: HttpRequest)(implicit config: ApplicationConfig,
                                                   transactionProducer: KafkaProducer[String, String],
                                                   m: Materializer,
                                                   system: ActorSystem,
                                                   dateTimeFormatter: DateTimeFormatter,
                                                   ec: ExecutionContext): Future[_] =
    stringify(request)
      .flatMap(parseJson)
      .flatMap(toKeyAndStringifedTransaction)
      .map{ case (key, value) => new ProducerRecord(config.transactionsByIdTopic, key, value)}
      .flatMap(sendToKafka(transactionProducer))
      //.flatMap(_ => Future(HttpResponse(StatusCodes.OK)).recoverWith(handleErrors))

  def readAllTransactions()(implicit actorSystem: ActorSystem,
                            config: ApplicationConfig): Future[Seq[PersistentTransaction]] = {

    Consumer.plainSource(
      ConsumerSettings(actorSystem, new StringDeserializer, new StringDeserializer)
        .withBootstrapServers(config.kafkaBootstrapServers)
        .withGroupId(randomizedGroupId(config.kafkaGroupId))
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"),
      Subscriptions.topics(config.transactionsByIdTopic)
    ).takeWithin(config.transactionsReadTimeout)
      .map(record => JsonParser(record.value()).convertTo[PersistentTransaction])
      .toMat(Sink.seq)(Keep.right)
      .run()
  }

  private def randomizedGroupId(basicId: String): String = basicId + UUID.randomUUID().toString

  private def sendToKafka[K, V](producer: KafkaProducer[K, V])
                               (record: ProducerRecord[K, V])
                               (implicit ex: ExecutionContext): Future[_] = asScala(
    Futurity.shift(producer.send(record)))
    .map(_ => producer.flush())
    .recoverWith(t => Future.failed(TransactionException(
        UnableToSendToKafka, s"Unable to produce a record to Kafka: $record", t))
    )

    private def toKeyAndStringifedTransaction(jsValue: JsValue)
                                             (implicit ec: ExecutionContext,
                                              formatter: DateTimeFormatter): Future[(String, String)] =
      Future(jsValue.convertTo[Transaction]).map(PersistentTransaction.fromTransaction).recoverWith(t => Future.failed(
          TransactionException(NotATransaction, s"JSON does not represent a Transaction: ${jsValue.prettyPrint}", t))
      ).map(transaction => (transaction.id, transaction))
        .map { case (key, value) => (key, value.toJson.toString)}

    private def parseJson(s: String)(implicit executionContext: ExecutionContext): Future[JsValue] =
      Future(s)
        .map(JsonParser(_))
        .recoverWith(t => Future.failed(
          TransactionException(TransactionErrorCode.NonJsonBody, s"Request body is not a correct JSON", t)
        ))

    private def stringify(request: HttpRequest)(implicit m: Materializer, ec: ExecutionContext): Future[String] =
      Unmarshal(request.entity)
        .to[String]
        .recoverWith(t => Future.failed(TransactionException(
          TransactionErrorCode.IncorrectRequestEntityType, s"Incorrect Entity Type: ${request.entity.contentType}", t
        )))
}
