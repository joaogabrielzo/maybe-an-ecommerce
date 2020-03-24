package com.zo.server

import akka.http.scaladsl.Http
import com.zo.database.DBSetup
import com.zo.routes.StockRoute
import com.zo.service.HttpService

object MainServer extends App
                  with HttpService {

//    DBSetup.startStockTable()
    
    Http().bindAndHandle(StockRoute.stockRoute, "localhost", 9999)
}
