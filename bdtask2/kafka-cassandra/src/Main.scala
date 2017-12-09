import java.util.Properties
import java.util.Arrays
import java.util.Date
import java.util.UUID

import scala.collection.JavaConverters._

import org.apache.kafka.clients.consumer._
import com.datastax.driver.core._

/**
 * Kafka-Cassandra proxy daemon.
 * Main application object.
 */
object Main extends App {
  val defaultKafkaServer = "0.0.0.0:9092"
  val defaultCassandraHost = "0.0.0.0"
  val defaultCassandraPort = 9042

  val topic = "twitter"

  /**
   * Main function
   * @param kafkaServer - host and port of kafka server (default: 0.0.0.0:9092)
   * @param cassandraHost - host of cassandra server (default: 0.0.0.0)
   * @param cassandraPort - port of cassandra server (default: 9042)
   */
  def run(kafkaServer: String = defaultKafkaServer, cassandraHost: String = defaultCassandraHost,
          cassandraPort: Int = defaultCassandraPort) {
    val props = new Properties
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "kafka-cassandra")
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")

    val consumer = new KafkaConsumer[Nothing, String](props) 

    val cassandraSession = Cluster
      .builder
      .addContactPointsWithPorts(new java.net.InetSocketAddress(cassandraHost, cassandraPort))
      .build
      .connect("BDTASK")

    lazy val metricInsertStatement = cassandraSession.prepare("insert into event (id, time) values (?, ?)")

    consumer.subscribe(Arrays.asList(topic))
    while(true) {
      /**
       * Fetch records from kafka and write them into cassandra in one batch
       */
      val records = consumer.poll(100)
      val batch = new BatchStatement
      for (record <- records.asScala) {
        println(record.value)
        batch.add(new BoundStatement(metricInsertStatement).bind(UUID.randomUUID, new Date(record.value.toLong)))
      }
      cassandraSession.execute(batch)
    }
  }

  args.length match {
    case 0 => run()
    case 1 => run(args(0))
    case 2 => run(args(0), args(1))
    case _ => run(args(0), args(1), args(2).toInt)
  }
}
