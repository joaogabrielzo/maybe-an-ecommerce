package com.zo.service

import akka.actor.{Actor, ActorLogging}
import com.zo.database.DBSetup._
import com.zo.service.StockActor._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object StockActor {
    case class InsertProduct(product: Stock)
    case class ProductInserted(product: Stock)
    case class SelectProduct(productId: String)
    case object SelectAll
    case class UpdateProduct(product: Stock)
    case class DeleteProduct(productId: String)
}

class StockActor extends Actor with ActorLogging {
    
    override def receive: Receive = {
        case InsertProduct(product) =>
            val insertProduct: Stock = StockQueries.insertProduct(product)
            
            sender() ! product
        
        case SelectProduct(productId) =>
            val selectedProduct: Option[Stock] = StockQueries.selectProductById(productId)
            
            sender() ! selectedProduct
        
        case SelectAll =>
            val allProducts: Seq[Stock] = StockQueries.selectAll
            
            sender() ! allProducts
        
        case UpdateProduct(product)
        =>
            val updateProduct = StockQueries.updateProduct(product)
            
            sender() ! product
        
        case DeleteProduct(productId)
        =>
            val deleteProduct: Int = StockQueries.deleteProductById(productId)
            
            sender() ! deleteProduct.toString
    }
    
}

