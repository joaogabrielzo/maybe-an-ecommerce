package com.zo.database

import com.zo.config.{DB, PG}
import slick.lifted._
import slick.sql.{FixedSqlAction, FixedSqlStreamingAction}

import scala.concurrent.{ExecutionContext, Future}

case class Product(
                      productId: String,
                      description: String,
                      quantity: Int,
                      unitPrice: Double
                  )

trait StockTableDefinition {
    this: DB =>
    
    import driver.api._
    
    class StockTable(tag: Tag) extends Table[Product](tag, "stock") {
        
        def id: Rep[String] = column[String]("product_id", O.PrimaryKey, O.Unique)
        def description: Rep[String] = column[String]("description")
        def quantity: Rep[Int] = column[Int]("quantity")
        def unitPrice: Rep[Double] = column[Double]("unit_price")
        
        def idIndex = index("product_id", id, unique = true)
        
        def * : ProvenShape[Product] = (
                                           id, description, quantity, unitPrice
                                       ) <> (Product.tupled, Product.unapply)
        
    }
    
}
abstract class StockRepository(implicit ex: ExecutionContext)
    extends PG with StockTableDefinition {
    
    import driver.api._
    
    
    lazy val stockTable: TableQuery[StockTable] = TableQuery[StockTable]
    
    def insertProduct(product: Product): Future[Int] = db.run(stockTable += product)
    
    def selectAllProducts: Future[Seq[Product]] = db.run(stockTable.result)
    
    def selectProduct(productId: String): Future[Option[Product]] =
        db.run(stockTable.filter(_.id === productId).result.headOption)
    
    def updateProduct(product: Product): Future[Int] = db.run(stockTable.insertOrUpdate(product))
    
    def deleteProduct(productId: String): Future[Boolean] =
        db.run(stockTable.filter(_.id === productId).delete.map(_ > 0))
    
}

