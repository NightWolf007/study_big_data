#!/bin/bash

slaves="$1"
mode="$2"

file_slaves='/usr/local/hadoop/etc/hadoop/slaves'

echo -n '' > "$file_slaves"
for i in `seq 1 "$slaves"`; do echo "hadoop-slave$i" >> "$file_slaves"; done

mkdir -p /usr/local/hadoop/logs/userlogs /tmp/hadoop-root/nm-local-dir || exit $?
service ssh start || exit $?
[ "$mode" == "master" ] && { ./start-hadoop.sh || exit $?; }
while [ "`find /usr/local/hadoop/logs -name \"*.log\" | wc -l`" -lt 2 ]; do sleep 1; done
find /usr/local/hadoop/logs -name "*.log" | xargs tail -f 2> /dev/null || exit $?
