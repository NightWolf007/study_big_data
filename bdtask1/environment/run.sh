#!/bin/bash

docker cp ../target/scala-2.12/bdtask1-assembly-1.0.jar hadoop-master:/opt/bdtask1.jar || exit $?
docker cp ../log_generator.py hadoop-master:/opt || exit $?
docker exec hadoop-master python3 /opt/log_generator.py /opt/data1.log 1000 || exit $?
docker exec hadoop-master python3 /opt/log_generator.py /opt/data2.log 2000 || exit $?
docker exec hadoop-master python3 /opt/log_generator.py /opt/data3.log 3000 || exit $?
docker exec hadoop-master python3 /opt/log_generator.py /opt/data4.log 4000 || exit $?
docker exec hadoop-master python3 /opt/log_generator.py /opt/data5.log 5000 || exit $?
docker exec hadoop-master /usr/local/hadoop/bin/hadoop fs -rm -r /input_dir
docker exec hadoop-master /usr/local/hadoop/bin/hadoop fs -mkdir /input_dir || exit $?
docker exec hadoop-master /usr/local/hadoop/bin/hadoop fs -put /opt/data1.log /input_dir/data1.log || exit $?
docker exec hadoop-master /usr/local/hadoop/bin/hadoop fs -put /opt/data2.log /input_dir/data2.log || exit $?
docker exec hadoop-master /usr/local/hadoop/bin/hadoop fs -put /opt/data3.log /input_dir/data3.log || exit $?
docker exec hadoop-master /usr/local/hadoop/bin/hadoop fs -put /opt/data4.log /input_dir/data4.log || exit $?
docker exec hadoop-master /usr/local/hadoop/bin/hadoop fs -put /opt/data5.log /input_dir/data5.log || exit $?
docker exec hadoop-master /usr/local/hadoop/bin/hadoop fs -rm -r /output_dir
docker exec hadoop-master /usr/local/hadoop/bin/hadoop jar /opt/bdtask1.jar /input_dir /output_dir || exit $?
docker exec hadoop-master /usr/local/hadoop/bin/hadoop fs -text /output_dir/part-r-00000
