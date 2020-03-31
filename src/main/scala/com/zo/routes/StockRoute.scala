package com.zo.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.zo.database.{JsonProtocol, Product, StockTableDefinition}
import spray.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success}

class StockRoute(repo: StockTableDefinition#StockRepository)(implicit ec: ExecutionContext)
    extends JsonProtocol {
    
    val stockRoute: Route =
        pathPrefix("stock") {
            path("all") {
                val allProducts: Future[Seq[Product]] = repo.selectAll
                
                onComplete(allProducts) {
                    case Success(products) =>
                        complete(products)
                    case Failure(ex)       =>
                        complete(s"Select query failed with: $ex")
                }
            } ~
            (path(Segment) | parameter("productId")) { productId =>
                val selectedProduct: Future[Option[Product]] = repo.selectId(productId)
                
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
            post {
                entity(as[Product]) { product =>
                    val insertedProduct: Future[Int] = repo.insert(product)
                    
                    onComplete(insertedProduct) {
                        case Success(_)  =>
                            complete(
                                HttpResponse(
                                    StatusCodes.Created,
                                    entity = s"Object with ID ${product.id} was successfully inserted."))
                        case Failure(ex) =>
                            complete(s"Insert operation failed with: $ex")
                    }
                }
            } ~
            put {
                entity(as[Product]) { product =>
                    val updatedProduct: Future[Int] = repo.update(product)
                    
                    onComplete(updatedProduct) {
                        case Success(_)  =>
                            complete(
                                HttpResponse(
                                    StatusCodes.OK,
                                    entity = s"Object with ID ${product.id} was successfully updated."))
                        case Failure(ex) =>
                            complete(s"Update operation failed with: $ex")
                    }
                }
            } ~
            path(Segment) { productId =>
                delete {
                    val deletedProduct: Future[Boolean] = repo.deleteId(productId)
                    
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
