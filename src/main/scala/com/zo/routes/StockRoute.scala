package com.zo.routes

import akka.actor.{ActorRef, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import com.zo.database.DBSetup.Stock
import com.zo.database.StockJsonProtocol
import com.zo.service.StockActor._
import com.zo.service.{HttpService, StockActor}
import spray.json._

import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.{Failure, Success}

object StockRoute extends StockJsonProtocol
                  with HttpService
                  with SprayJsonSupport {
    
    val stockActor: ActorRef = system.actorOf(Props[StockActor], "stock-actor")
    
    val stockRoute: Route =
        pathPrefix("stock") {
            path("all") {
                val selectAllProducts: Future[Seq[Stock]] = (stockActor ? SelectAll).mapTo[Seq[Stock]]
                
                onComplete(selectAllProducts) {
                    case Success(products) =>
                        complete(products)
                    case Failure(ex)       =>
                        complete(s"Select query failed with: $ex")
                }
            } ~
            (path("find" / Segment) | parameter("productId")) { productId =>
                val selectedProduct: Future[Option[Stock]] =
                    (stockActor ? SelectProduct(productId)).mapTo[Option[Stock]]
                
                onComplete(selectedProduct) {
                    case Success(productOption) =>
                        productOption match {
                            case Some(product) =>
                                complete(product.toJson.prettyPrint)
                            case None          =>
                                complete(s"No object with ID $productId was found.")
                        }
                    case Failure(ex)            =>
                        complete(s"Select query failed with: $ex")
                }
            } ~
            path("insert") {
                post {
                    entity(as[Stock]) { product =>
                        val insertProduct: Future[Stock] = (stockActor ? InsertProduct(product)).mapTo[Stock]
                        
                        onComplete(insertProduct) {
                            case Success(_)  =>
                                complete(
                                    HttpResponse(
                                        StatusCodes.Created,
                                        entity = s"Object with ID ${product.productId} was successfully inserted."))
                            case Failure(ex) =>
                                complete(s"Insert operation failed with: $ex")
                        }
                    }
                }
            } ~
            path("update") {
                put {
                    entity(as[Stock]) { product =>
                        val updateProduct: Future[Stock] = (stockActor ? UpdateProduct(product)).mapTo[Stock]
                        
                        onComplete(updateProduct) {
                            case Success(_)  =>
                                complete(
                                    HttpResponse(
                                        StatusCodes.OK,
                                        entity = s"Object with ID ${product.productId} was successfully updated."))
                            case Failure(ex) =>
                                complete(s"Update operation failed with: $ex")
                        }
                    }
                }
            } ~
            path("delete" / Segment) { productId =>
                delete {
                    
                    val deleteProduct: Future[String] = (stockActor ? DeleteProduct(productId)).mapTo[String]
                    
                    onComplete(deleteProduct) {
                        case Success(_)  =>
                            complete(
                                HttpResponse(
                                    StatusCodes.OK,
                                    entity = s"Object with ID $productId was successfully deleted."))
                        case Failure(ex) =>
                            complete(s"Delete operation failed with: $ex")
                    }
                }
            }
            
        }
}
