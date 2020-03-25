package com.zo.database

trait Entity {
    
    def id: String
}

case class Product(
                      id: String,
                      description: String,
                      quantity: Int,
                      unitPrice: Double
                  ) extends Entity
