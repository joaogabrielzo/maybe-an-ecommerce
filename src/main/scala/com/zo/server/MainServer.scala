package com.zo.server

import akka.http.scaladsl.Http
import com.zo.config.{EcommerceConfig, MSQL}
import com.zo.service.HttpService

object MainServer extends App
                  with HttpService
                  with EcommerceConfig
                  with MSQL {
    
    Http().bindAndHandle(stockRoute, httpHost, httpPort)
}
