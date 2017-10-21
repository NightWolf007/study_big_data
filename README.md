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
