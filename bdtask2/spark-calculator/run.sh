#!/bin/bash

docker cp target/scala-2.11/spark-calculator-assembly-1.0.jar spark:/opt/bdtask.jar || exit $?
docker-compose exec spark /usr/local/spark/bin/spark-submit --class Main --master yarn-client --driver-memory 1g --executor-memory 1g --executor-cores 1 --deploy-mode client /opt/bdtask.jar || exit $?

