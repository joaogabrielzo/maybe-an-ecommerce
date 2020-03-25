package com.zo.config

import com.zo.database.Entity
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag


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

trait TableDefinition {
    this: DB =>
    
    import driver.api._
    
    abstract class BaseTable[E <: Entity : ClassTag](
                                                        tag: Tag, tableName: String
                                                    ) extends Table[E](tag, tableName) {
        
        val id = column[String]("id", O.PrimaryKey, O.Unique)
        
        def idIndex = index("id", id, unique = true)
    }
}

trait Repository[E <: Entity] {
    
    def insert(entity: E): Future[Int]
    def selectAll: Future[Seq[E]]
    def selectId(id: String): Future[Option[E]]
    def update(entity: E): Future[Int]
    def deleteId(id: String): Future[Boolean]
}

trait RepoDefinition extends TableDefinition {
    this: DB =>
    
    import driver.api._
    
    abstract class BaseRepo[E <: Entity, T <: BaseTable[E]](implicit ex: ExecutionContext)
        extends Repository[E] {
        
        val table: TableQuery[T]
        
        override def insert(entity: E): Future[Int] = db.run(table += entity)
        
        override def selectAll: Future[Seq[E]] = db.run(table.result)
        
        override def selectId(id: String): Future[Option[E]] = db.run(table.filter(_.id === id).result.headOption)
        
        override def update(entity: E): Future[Int] = db.run(table.insertOrUpdate(entity))
        
        override def deleteId(id: String): Future[Boolean] = db.run(table.filter(_.id === id).delete.map(_ > 0))
    }
}
