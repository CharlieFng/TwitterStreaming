name := "TwitterStreaming"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= {
  val sparkVersion = "2.2.0"
  val flinkVersion = "1.6.0"
  Seq(
    "org.apache.spark" %% "spark-core" % sparkVersion,
    "org.apache.spark" %% "spark-sql" % sparkVersion,
    "org.apache.spark" %% "spark-streaming" % sparkVersion,
    "org.apache.bahir" %% "spark-streaming-twitter" % sparkVersion,
    "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion,
    "org.apache.flink" %% "flink-scala" % flinkVersion,
    "org.apache.flink" %% "flink-streaming-scala" % flinkVersion,
    "org.apache.flink" %% "flink-clients" % flinkVersion,
    "org.apache.flink" %% "flink-connector-kafka-0.10" % flinkVersion,
    "org.apache.flink" % "flink-jdbc" % flinkVersion,
    "org.twitter4j" % "twitter4j-core" % "4.0.4",
    "org.twitter4j" % "twitter4j-async" % "4.0.4",
    "org.twitter4j" % "twitter4j-stream" % "4.0.4",
    "org.apache.kafka" % "kafka-clients" % "1.1.0",
    "org.postgresql" % "postgresql" % "42.2.4"
  )
}