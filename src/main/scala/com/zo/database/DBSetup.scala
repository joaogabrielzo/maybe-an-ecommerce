package com.zo.database

import slick.dbio.Effect
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ProvenShape, Tag}
import slick.sql.FixedSqlAction

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps

object DBSetup {
    
    case class Stock(
                        productId: String,
                        description: String,
                        quantity: Int,
                        unitPrice: Double
                    )
    
    class StockTable(tag: Tag) extends Table[Stock](tag, "stock") {
        
        def productId: Rep[String] = column[String]("product_id", O.PrimaryKey, O.Unique)
        def description: Rep[String] = column[String]("description")
        def quantity: Rep[Int] = column[Int]("quantity")
        def unitPrice: Rep[Double] = column[Double]("unit_price")
        
        def * : ProvenShape[Stock] = (
                                         productId, description, quantity, unitPrice
                                     ) <> (Stock.tupled, Stock.unapply)
    }
    
    lazy val StockTable: TableQuery[StockTable] = TableQuery[StockTable]
    
    lazy val db = Database.forConfig("database")
    
    val createTable: FixedSqlAction[Unit, NoStream, Effect.Schema] = StockTable.schema.createIfNotExists
    
    object StockQueries {
        
        def insertProduct(product: Stock): Stock =
            exec(StockTable returning StockTable += product)
        
        def selectProductById(productId: String): Option[Stock] =
            exec(StockTable.filter(_.productId === productId).result.headOption)
        
        def selectAll: Seq[Stock] =
            exec(StockTable.result)
        
        def updateProduct(product: Stock): Int =
            exec(StockTable.insertOrUpdate(product))
        
        def deleteProductById(productId: String): Int =
            exec(StockTable.filter(_.productId === productId).delete)
    }
    
    def exec[T](action: DBIO[T]): T =
        Await.result(db.run(action), 3 seconds)
    
    private def startStockTable: Unit = {
        exec(createTable)
        
        val stockData = io.Source.fromFile("src/main/resources/data/stock-data.csv")
        for (line <- stockData.getLines) {
            val cols = line.split(",").map(_.trim)
            exec(
                StockTable.insertOrUpdate(
                    Stock(
                        cols(0), cols(1), cols(2).toInt, cols(3).toDouble)
                )
            )
        }
    }
    
}
