#!/bin/bash

docker cp ../target/scala-2.12/bdtask1-assembly-1.0.jar hadoop-master:/opt/bdtask1.jar || exit $?
docker cp ../log_generator.py hadoop-master:/opt || exit $?
docker exec hadoop-master python3 /opt/log_generator.py /opt/data.log 1000 || exit $?
docker exec hadoop-master /usr/local/hadoop/bin/hadoop fs -rm -r /input_dir
docker exec hadoop-master /usr/local/hadoop/bin/hadoop fs -mkdir /input_dir || exit $?
docker exec hadoop-master /usr/local/hadoop/bin/hadoop fs -put /opt/data.log /input_dir/data.log || exit $?
docker exec hadoop-master /usr/local/hadoop/bin/hadoop fs -rm -r /output_dir
docker exec hadoop-master /usr/local/hadoop/bin/hadoop jar /opt/bdtask1.jar /input_dir /output_dir || exit $?
docker exec hadoop-master /usr/local/hadoop/bin/hadoop fs -text /output_dir/part-r-00000
