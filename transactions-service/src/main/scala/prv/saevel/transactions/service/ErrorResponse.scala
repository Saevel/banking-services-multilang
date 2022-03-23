package prv.saevel.transactions.service

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

case class ErrorResponse(errorCode: String, errorMessage: String)

object ErrorResponse extends SprayJsonSupport {
  implicit val jsonFormat: RootJsonFormat[ErrorResponse] = jsonFormat2(ErrorResponse.apply _)
}