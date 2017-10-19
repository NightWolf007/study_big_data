#!/bin/bash

jar_name="bdtask-assembly-0.1.0-SNAPSHOT.jar"
sbt assembly && \
  docker cp ./target/$jar_name sandbox:/root && \
  (
    docker exec sandbox /usr/bin/hadoop fs -rm -r /output_dir;
    docker exec sandbox /usr/bin/hadoop jar /root/$jar_name /input_dir /output_dir
  )
