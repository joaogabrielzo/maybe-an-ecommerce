package com.zo.routes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.zo.database.{Product, ProductJsonProtocol, StockRepository}
import com.zo.service.HttpService
import spray.json._

import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object StockRoute extends StockRepository
                  with HttpService
                  with ProductJsonProtocol
                  with SprayJsonSupport {
    
    val stockRoute: Route =
        pathPrefix("stock") {
            path("all") {
                val allProducts: Future[Seq[Product]] = selectAllProducts
                
                onComplete(allProducts) {
                    case Success(products) =>
                        complete(products)
                    case Failure(ex)       =>
                        complete(s"Select query failed with: $ex")
                }
            } ~
            (path("find" / Segment) | parameter("productId")) { productId =>
                val selectedProduct: Future[Option[Product]] = selectProduct(productId)
                
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
                    entity(as[Product]) { product =>
                        val insertedProduct: Future[Int] = insertProduct(product)
                        
                        onComplete(insertedProduct) {
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
                    entity(as[Product]) { product =>
                        val updatedProduct: Future[Int] = updateProduct(product)
                        
                        onComplete(updatedProduct) {
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
                    
                    val deletedProduct: Future[Boolean] = deleteProduct(productId)
                    
                    onComplete(deletedProduct) {
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
