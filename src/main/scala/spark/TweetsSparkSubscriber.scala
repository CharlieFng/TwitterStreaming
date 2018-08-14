package spark

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

object TweetsSparkSubscriber {

  val conf = new SparkConf()
                .setMaster("local[3]")
                .setAppName("Twitter Streaming")
                .set("spark.executor.memory", "1g")

  val sc = new SparkContext(conf)

  val kafkaParams = Map[String, Object](
                    "bootstrap.servers" -> "localhost:9092",
                    "key.deserializer" -> classOf[StringDeserializer],
                    "value.deserializer" -> classOf[StringDeserializer],
                    "group.id" -> "nba",
                    "auto.offset.reset" -> "latest",
                    "enable.auto.commit" -> (false: java.lang.Boolean)
                  )

  val topics = Set("NBA_Topic")


  def main(args: Array[String]): Unit = {

    sc.setLogLevel("WARN")
    val ssc = new StreamingContext(sc, Seconds(2))
    ssc.checkpoint("checkpoint")


    val stream = KafkaUtils.createDirectStream[String,String](
      ssc,
      PreferConsistent,
      Subscribe[String,String](topics,kafkaParams)
    )


    stream.map(_.value()).foreachRDD ( rdd => {
      rdd.foreachPartition(records => {
        val conn = JDBCConnPool.getConnection
        conn.setAutoCommit(false)
        val sql = "INSERT INTO public.nba (message) VALUES (?)"
        val stmt = conn.prepareStatement(sql)
        records.foreach(record => {
          print(record)
          stmt.setString(1,record)
          stmt.addBatch()
        })
        stmt.executeBatch()
        conn.commit()
        JDBCConnPool.returnConnection(conn)
      }
      )
    }
    )

//    val wordCounts = stream.map(_.value)
//                           .flatMap(_.split(" "))
//                           .map((_,1))
//                           .reduceByKey(_ + _)

//
//    wordCounts.print()


    ssc.start()
    ssc.awaitTermination()


  }

}
