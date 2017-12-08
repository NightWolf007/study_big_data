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
  val topic = "twitter"

  def run() {
    val props = new Properties
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "0.0.0.0:9092")
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "kafka-cassandra")
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")

    val consumer = new KafkaConsumer[Nothing, String](props) 

    val cassandraSession = Cluster
      .builder
      .addContactPointsWithPorts(new java.net.InetSocketAddress("0.0.0.0", 9042))
      .build
      .connect("BDTASK")

    lazy val metricInsertStatement = cassandraSession.prepare("insert into event (id, time) values (?, ?)")

    consumer.subscribe(Arrays.asList(topic))
    while(true) {
      val records = consumer.poll(100)
      val batch = new BatchStatement
      for (record <- records.asScala) {
        batch.add(new BoundStatement(metricInsertStatement).bind(UUID.randomUUID, new Date(record.value.toLong)))
      }
      cassandraSession.execute(batch)
    }
  }

  run()
}
