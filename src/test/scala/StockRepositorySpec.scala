import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.zo.config.{H2, MSQL}
import com.zo.database.{Product, StockTableDefinition}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.ExecutionContext

class StockRepositorySpec extends TestKit(ActorSystem("test-stock"))
                          with AnyWordSpecLike
                          with Matchers
                          with StockTableDefinition
                          with ScalaFutures
                          with MSQL {
    
    implicit val ec: ExecutionContext = system.dispatcher
    
    val stockRepository = new StockRepository
    
    implicit val defaultPatience: PatienceConfig =
        PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))
    
    "A in memory database repository" should {
        "return all products" in {
            whenReady(stockRepository.selectAll)(_.size shouldBe 57)
        }
        
        "return a product by id" in {
            whenReady(stockRepository.selectId("10002")) { product =>
                product.get.description shouldBe "INFLATABLE POLITICAL GLOBE"
            }
        }
        
        "insert a product" in {
            val productToInsert = Product("Z090", "Test Input", 1, 99.99)
            
            val insertRes: Int = stockRepository.insert(productToInsert).futureValue
            
            insertRes shouldBe 1
            
            whenReady(stockRepository.selectId("Z090"))(_.get.description shouldBe "Test Input")
        }
        
        "update a product value" in {
            val productToUpdate = Product("Z090", "Test Update", 1, 99.99)
            
            whenReady(stockRepository.update(productToUpdate))(res => res shouldBe 1)
            
            whenReady(stockRepository.selectId("Z090"))(_.get.description shouldBe "Test Update")
        }
        
        "delete a product" in {
            whenReady(stockRepository.deleteId("Z090"))(res => res shouldBe true)
        }
    }
}
