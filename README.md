# Task 1

## Conditions

* Input data — log file with HTTP requests
* Output data — SequenceFile in format "IP,AVERAGE_BYTES_PER_REQUEST,TOTAL_BYTES".

### Additional conditions

* Custom Type must be defined and used

### Report includes

* ZIP-ed src folder with your implementation
* Screenshot of successfully executed tests
* Screenshot of successfully uploaded file into HDFS
* Screenshots of successfully executed job and result
* Quick build and deploy manual

## Deployment

### Requirements

* Linux-based OS
* `docker` and `docker-compose` installed
* `jdk8` and `sbt` installed

### Cluster configuration

To create Hadoop cluster `kiwenlau/hadoop` docker image was used.
This image was modified to support Java 8.
You need to run ./build.sh script from `environment` directory to build image.

To generate docker-compose.yml file you need to run `./configure.sh DATA_NODES_COUNT` from 'environment' directory.
Where DATA_NODES_COUNT is count of datanodes you need in cluster.
For example, you need to run `./configure.sh 2` to generate docker-compose.yml with two datanodes.

After that all you need is to run `docker-compose up` command and your cluster ready.
You can use command `docker-compose down` to stop the cluster.

### Build

To build the project you can use command `sbt assembly` from the `bdtask1` directory.
sbt-assembly will automaticly load all dependencies, run unit-tests and build standalone jar file to run on Hadoop cluster.

[Unit-test results.](bdtask1/screenshots/tests.png)

### Run

To run the project you can use prepared script `./run.sh` in the `environment` directory.
It will load project's jar file to hadoop masternode, generate log, load log to HDFS and run the jar file.

[Loaded file.](bdtask1/screenshots/file_in_hdfs.png)

[Job execution.](bdtask1/screenshots/job_execution.png)

You can take a look on the results after the job execution.
`./run.sh` script will automaticly print results of the execultion onto your screen.

[Job result.](bdtask1/screenshots/job_result.png)


# Task 2

## Condition

### Data

* Input data — [Twitter Public Stream Messages](https://developer.twitter.com/en/docs/tweets/filter-realtime/guides/streaming-message-types)
* Output data - Count of messages with keyword "bitcoin" per minute

### Technologies

* Data loading - Apache Kafka
* Data storage - Apache Cassandra
* Data processing - Apache Spark Streaming

### Report contains

* Source code
* Screenshot of unit test output
* Screenshot of job output
* Deployment manual
* Architecture diagram

## Architecture

Kafka-producer service receives messages from twitter and sends timestamps to Apache Kafka.
Kafka-cassandra service receives timestamps from Apache Kafka and writes it into Apache Cassandra.
Spark-calculator app fetches timestamps from Apache Cassandra and calculates count of messages per second with Spark Streaming.

[Architecture diagram.](bdtask2/screenshots/architecture.png)

## Deployment

### Requirements

* Linux-based OS
* `docker` and `docker-compose` installed
* `jdk8` and `sbt` installed

### Configuration

You need to have prepared Twitter application with `Consumer Key` and `Access Token`

Then you just need to export necessary enivronment variables.
```
export T_CONSUMER_KEY="Your Consumer Key"
export T_CONSUMER_SECRET="Your Consumer Secret"
export T_TOKEN="Your Access Token"
export T_SECRET="Your Access Token Secret"
```

### Build

You need to run command `sbt assembly` from `spark-calculator` directory.
Unit-tests will be passed on project's building.

[Unit-tests results](bdtask2/screenshots/tests.png)

### Run system

To run system you need just to run bash script from the project's directory `./run.sh`.
This script will start: Apache Kafka, Apache Cassandra, Apache Spark, Kafka-producer service and Kafka-cassandra service.

### Run job

To run job you need to run bash script `./spark-calculator/run.sh`

[Job's output](bdtask2/screenshots/job_output.png)
