package flink

import java.sql.Types
import org.apache.flink.api.java.io.jdbc.JDBCOutputFormat


object Sink {

  val query = "INSERT INTO public.nba (message) VALUES (?)"

  val postgresSink = JDBCOutputFormat.buildJDBCOutputFormat()
    .setDrivername("org.postgresql.Driver")
    .setDBUrl("jdbc:postgresql://localhost:5433/tweets?user=postgres&password=postgres")
    .setQuery(query)
    .setSqlTypes(Array(Types.VARCHAR))
    .setBatchInterval(1)
    .finish()

}
