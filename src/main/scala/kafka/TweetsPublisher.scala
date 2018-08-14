package kafka

import java.util.Properties
import java.util.concurrent.LinkedBlockingDeque

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import twitter4j._
import twitter4j.conf.ConfigurationBuilder

object TweetsPublisher {

  val consumerKey = "vaMuZVUj3YCzZ4EgzRf2P8fs5"
  val consumerSecret = "1bCiWH4r5UPTxUrCeKWPC7q1a8adKhoxp2LpwylCRO781gU2Ch"
  val accessToken = "705736451313373184-ZaZG2hlZ22qXGxuCd3RVJdQeD0NAtAe"
  val accessTokenSecret = "HCRS7dJgEjk6h8NhnesZ0plwPOv0l5hTF96c1f1QDD4fx"


  //Twitter setting
  val configurationBuilder = new ConfigurationBuilder()
    .setDebugEnabled(true)
    .setOAuthConsumerKey(consumerKey)
    .setOAuthConsumerSecret(consumerSecret)
    .setOAuthAccessToken(accessToken)
    .setOAuthAccessTokenSecret(accessTokenSecret)

  //Create twitterstream using configuration
  val twitterStream = new TwitterStreamFactory(configurationBuilder.build()).getInstance()

  final val blockingQueue = new LinkedBlockingDeque[Status](1000)

  val statusListener = new StatusListener {
    override def onStatus(status: Status): Unit = blockingQueue.offer(status)

    override def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit = println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId )

    override def onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit = println("Got track limitation notice:" + numberOfLimitedStatuses)

    override def onScrubGeo(userId: Long, upToStatusId: Long): Unit = println("Got scrub_geo event userId:" + userId + "upToStatusId:" + upToStatusId)

    override def onStallWarning(warning: StallWarning): Unit = println("Got stall warning:" + warning)

    override def onException(ex: Exception): Unit = ex.printStackTrace()
  }


  val query = new FilterQuery().track("Golden State Warriors", "LeBron James")


  val properties = new Properties()
  properties.put("bootstrap.servers", "localhost:9092")
  properties.put("key.serializer", classOf[StringSerializer])
  properties.put("value.serializer", classOf[StringSerializer])
  properties.put("acks", "1")
  properties.put("retries", "3")
  properties.put("linger.ms", "1")
  properties.put("batch.size", "16384")
  properties.put("buffer.memory", "33554432")


  val producer = new KafkaProducer[String,String](properties)


  def main(args: Array[String]): Unit = {

    twitterStream.addListener(statusListener)

    twitterStream.filter(query)

    while (true) {
      val ret = blockingQueue.poll()

      if(ret == null) Thread.sleep(100)
      else{
        ret.getHashtagEntities.foreach(record => {
        println("Tweet: " + ret)
        println("Hashtag: " + record.getText)
        val temp = new ProducerRecord[String, String]("NBA_Topic", record.getText, ret.getText)
        producer.send(temp)
        })
      }
    }

  }






}
