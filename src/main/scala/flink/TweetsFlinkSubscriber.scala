package flink

import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.apache.flink.types.Row

object TweetsFlinkSubscriber {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "localhost:9092")
    properties.setProperty("group.id", "nba")

    val consumer = new FlinkKafkaConsumer010[String]("NBA_Topic", new SimpleStringSchema, properties)

    val stream = env.addSource(consumer)

    stream.map(message => {
      println(message)
      val row = new Row(1);
      row.setField(0, message)
      row
    }).writeUsingOutputFormat(Sink.postgresSink)

//    val counts = stream.flatMap(_.split(" "))
//                        .map((_,1)).keyBy(0)
//                          .timeWindow(Time.seconds(5))
//                            .sum(1)
//
//    counts.print()

    env.execute("NBA Stream Count")

  }

}
