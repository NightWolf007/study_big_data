#!/bin/bash

jar_name="bdtask1-assembly-1.0.jar"
sbt assembly && \
  docker cp ./target/scala-2.12/$jar_name sandbox:/root && \
  (
    docker exec sandbox /usr/bin/hadoop fs -rm -r /output_dir;
    docker exec sandbox /usr/bin/hadoop jar /root/$jar_name /input_dir /output_dir
  )
