package com.zo.database

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait ProductJsonProtocol extends DefaultJsonProtocol {
    
    implicit val productFormat: RootJsonFormat[Product] = jsonFormat4(Product)
}
