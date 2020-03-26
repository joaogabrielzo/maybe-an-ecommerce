package com.zo.database

import com.zo.config.{DB, RepoDefinition}
import slick.lifted._

import scala.concurrent.ExecutionContext

trait StockTableDefinition extends RepoDefinition {
    this: DB =>
    
    import driver.api._
    
    class StockTable(tag: Tag) extends BaseTable[Product](tag, "stock") {
        
        def description: Rep[String] = column[String]("description")
        def quantity: Rep[Int] = column[Int]("quantity")
        def unitPrice: Rep[Double] = column[Double]("unit_price")
        
        def * : ProvenShape[Product] = (
                                           id, description, quantity, unitPrice
                                       ) <> (Product.tupled, Product.unapply)
        
    }
    
    class StockRepository(implicit ec: ExecutionContext) extends BaseRepo[Product, StockTable] {
        
        override val table: driver.api.TableQuery[StockTable] = TableQuery[StockTable]
        
    }
    
}

