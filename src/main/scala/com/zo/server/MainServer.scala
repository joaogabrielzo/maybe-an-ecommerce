package com.zo.server

import akka.http.scaladsl.Http
import com.zo.config.EcommerceConfig
import com.zo.routes.StockRoute
import com.zo.service.HttpService

object MainServer extends App
                  with HttpService
                  with EcommerceConfig {

//    DBSetup.startStockTable()
    
    Http().bindAndHandle(StockRoute.stockRoute, httpHost, httpPort)
}
