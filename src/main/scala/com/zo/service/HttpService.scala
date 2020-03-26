package com.zo.service

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.zo.config.DB
import com.zo.database.StockTableDefinition
import com.zo.routes.StockRoute

import scala.concurrent.ExecutionContextExecutor
import scala.language.postfixOps
import scala.concurrent.duration._

trait HttpService extends StockTableDefinition with DB {
    
    implicit val system: ActorSystem = ActorSystem()
    implicit val ec: ExecutionContextExecutor = system.dispatcher
    implicit val timeout: Timeout = Timeout(3 seconds)
    
    lazy val stockRepository = new StockRepository()
    
    lazy val stockRoute: Route = new StockRoute(stockRepository).stockRoute
}
