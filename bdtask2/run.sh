#!/bin/bash

docker-compose up -d
(cd kafka-producer; sbt "run $T_CONSUMER_KEY $T_CONSUMER_SECRET $T_TOKEN $T_SECRET") &
(cd kafka-cassandra; sbt run) &
