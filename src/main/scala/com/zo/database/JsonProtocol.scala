package com.zo.database

import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import com.zo.database.DBSetup.Stock

trait StockJsonProtocol extends DefaultJsonProtocol {
    
    implicit val stockFormat: RootJsonFormat[Stock] = jsonFormat4(Stock)
}
