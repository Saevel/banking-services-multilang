package prv.saevel.transactions.service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig}
import org.apache.kafka.common.serialization.StringSerializer

import java.time.format.DateTimeFormatter
import scala.util.{Failure, Success}

object Application extends App {

  import spray.json.DefaultJsonProtocol._
  import spray.json._

  import scala.concurrent.duration._
  import scala.jdk.CollectionConverters._

  private implicit val actorSystem: ActorSystem = ActorSystem("TransactionsService")

  private implicit val applicationConfig: ApplicationConfig = ApplicationConfig(
    4, "transactions-by-id", "kafka:9092", "transactions-service", 10.seconds
  )

  private implicit val transactionProducer: KafkaProducer[String, String] = new KafkaProducer[String, String](Map[String, java.lang.Object](
     ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> applicationConfig.kafkaBootstrapServers,
     ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG -> classOf[StringSerializer].getName,
     ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG -> classOf[StringSerializer].getName
  ).asJava)

  private implicit val formatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

  import scala.concurrent.ExecutionContext.Implicits._

  Http()
    .newServerAt("0.0.0.0", 8081)
    .bindFlow(routes)
    .onComplete {
      case Success(_) => println(s"Server running at: 0.0.0.0:8081")
      case Failure(e) => {
        println("Failed to run server at: 0.0.0.0:8081")
        println(e)
      }
    }

  def routes: Route = /**pathSingleSlash {
    get {
      complete("Hello, Scala!")
    }
  } ~ */ path("transactions") {
    post {
      extractRequest { request =>
        onComplete(TransactionStreams.persistTransactionById(request)){
          case Success(_) => complete(StatusCodes.OK)
          // TODO: Improve error handling
          case Failure(t) => {
            t.printStackTrace()
            complete(StatusCodes.InternalServerError)
          }
        }
      }
    } ~ get {
      onComplete(TransactionStreams.readAllTransactions()){
        case Success(transactions) => complete(StatusCodes.OK, transactions.toJson.toString())
        case Failure(t) => {
          t.printStackTrace()
          complete(StatusCodes.InternalServerError)
        }
      }
    }
  }

  /**

  private def handleErrors()(implicit tem: ToEntityMarshaller[ErrorResponse], ex: ExecutionContext): PartialFunction[Throwable, Future[HttpResponse]] = {
    case TransactionException(errorCode, message , cause) =>
      println(message)
      cause.printStackTrace()
      Marshal(ErrorResponse(errorCode.toString, message)).to[ResponseEntity].map(entity =>
        errorCode match {
          case IncorrectRequestEntityType | NonJsonBody | NotATransaction =>
            HttpResponse(StatusCodes.UnprocessableEntity, entity = entity)
          case UnableToSendToKafka =>
            HttpResponse(StatusCodes.InternalServerError, entity = entity)
        }
      )

    case other => {
      other.printStackTrace()
      Future(HttpResponse(StatusCodes.InternalServerError))
    }
  }
  */
}