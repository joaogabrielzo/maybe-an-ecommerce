package com.zo.config

import slick.jdbc.JdbcProfile


trait DB {
    
    val driver: JdbcProfile
    
    import driver.api._
    
    lazy val db: Database = Database.forConfig("database")
}

trait H2 extends DB {
    
    override val driver: JdbcProfile = slick.jdbc.H2Profile
}

trait PG extends DB {
    
    override val driver: JdbcProfile = slick.jdbc.PostgresProfile
}
