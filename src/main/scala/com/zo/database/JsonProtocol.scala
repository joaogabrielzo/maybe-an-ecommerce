package com.zo.database

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    
    implicit val productFormat: RootJsonFormat[Product] = jsonFormat4(Product)
}
