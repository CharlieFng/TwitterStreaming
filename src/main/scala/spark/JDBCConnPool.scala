package spark

import java.sql.{Connection, DriverManager}

import scala.collection.mutable

object JDBCConnPool {

  val connectionQueue:mutable.Queue[Connection] = mutable.Queue()

  def init = {
    try{
      Class.forName("org.postgresql.Driver")
    }catch{
      case e: ClassNotFoundException => e.printStackTrace()
    }
  }

  def getConnection: Connection = this.synchronized[Connection] {
    init
    if(connectionQueue.isEmpty){
      1 to 5 foreach { _ =>
        val conn = DriverManager.getConnection("jdbc:postgresql://localhost:5433/tweets", "postgres", "postgres")
        connectionQueue.enqueue(conn)
      }
    }
    connectionQueue.dequeue()
  }

  def returnConnection(conn: Connection) = connectionQueue.enqueue(conn)

}
