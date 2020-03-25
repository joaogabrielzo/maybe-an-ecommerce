package com.zo.server

import akka.http.scaladsl.Http
import com.zo.config.{EcommerceConfig, PG}
import com.zo.service.HttpService

object MainServer extends App
                  with HttpService
                  with EcommerceConfig
                  with PG {

//    DBSetup.startStockTable()
    
    Http().bindAndHandle(routes, httpHost, httpPort)
}
