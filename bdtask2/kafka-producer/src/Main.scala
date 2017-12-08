import java.util.Properties
import java.util.Arrays
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

import org.apache.kafka.clients.producer._

import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.Client
import com.twitter.hbc.core.Constants
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import com.twitter.hbc.httpclient.auth.Authentication
import com.twitter.hbc.httpclient.auth.OAuth1

/**
 * Kafka producer daemon.
 * Main application object.
 * It fetches messages from twitter and sends them to kafka.
 */
object Main extends App {
  val defaultKafkaServer = "0.0.0.0:9092"

  val topic = "twitter"
  val partition = 0
  val trackItem = "bitcoin"

  var timestampRegex = "\"timestamp_ms\":\"(\\d+)\"".r

  /**
   * Main function
   * consumerKey - twitter consumer key
   * consumerSecret - twitter consumer secret
   * token - twitter token
   * secret - twitter secret
   * kafkaServer - host and port of kafka server (default: 0.0.0.0:9092)
   */
  def run(consumerKey: String, consumerSecret: String, token: String, secret: String, kafkaServer: String = defaultKafkaServer) {
    val props = new Properties
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[Nothing, String](props)

    val queue = new LinkedBlockingQueue[String](10000)

    val endpoint = new StatusesFilterEndpoint()
    endpoint.trackTerms(Arrays.asList(trackItem))

    val auth = new OAuth1(consumerKey, consumerSecret, token, secret)
    val client = new ClientBuilder()
            .name("bdtask2")
            .hosts(Constants.STREAM_HOST)
            .endpoint(endpoint)
            .authentication(auth)
            .processor(new StringDelimitedProcessor(queue))
            .build()

    client.connect()

    println("Connected to Twitter")
    while (!client.isDone()) {
      println("NYAN")
      val msg = queue.take()
      println(msg)
      timestampRegex.findFirstMatchIn(msg)
                    .flatMap(_.subgroups.headOption)
                    .foreach(time => producer.send(new ProducerRecord[Nothing, String](topic, time)))
    }

    if (client.isDone()) {
      println("Twitter Client connection closed unexpectedly: " + client.getExitEvent.getMessage)
    }

    client.stop()
    producer.close()
  }

  if (args.length >= 5) {
    run(args(0), args(1), args(2), args(3), args(4))
  } else {
    run(args(0), args(1), args(2), args(3))
  }
}
