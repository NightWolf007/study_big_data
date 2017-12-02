#!/bin/bash

function cleanup() {
  rm -rfv docker-compose.yml
  rm -rfv cassandra-config/create.cql
  rm -rfv compute-config/compute.conf
}

function join-array() {
  delimeter="$1"
  shift
  echo -n "$1"
  shift
  [ "$#" -gt 0 ] && printf "$delimeter%s" "$@" || true
}

function is-number() {
  [ "`grep -Po '\d+' <<< "$1"`" == "$1" ]
}

[ "$1" == 'cleanup' ] && {
  cleanup
  exit 0
}

compute_jar='../compute/target/scala-2.12/compute.jar'
[ -e "$compute_jar" ] && cp "$compute_jar" 'compute-config/compute.jar' || {
  echo 'compute.jar not found.' >&2
  exit 1
}

source config.conf

[ -n "$storages" ] && is-number "$storages" && [ "$storages" -ge 1 ] && {
  cleanup

  list_cassandra_full=()

  for i in `seq 1 "$storages"`; do
    list_cassandra_full+=("cassandra$i:9042")
  done

  echo 'create cassandra-config/create.cql'
  cat cassandra-config/create.cql.in |
  sed -e "s/@PREFIX@/$prefix/g" \
  > cassandra-config/create.cql ||
  { echo 'Fail.' >&2; exit 1; }

  echo 'create compute-config/compute.conf'
  cat compute-config/compute.conf.in |
  sed -e "s/@CASSANDRA_LIST@/\"`join-array '", "' "${list_cassandra_full[@]}"`\"/g" \
  -e "s/@PREFIX@/$prefix/g" \
  > compute-config/compute.conf ||
  { echo 'Fail.' >&2; exit 1; }

  echo 'create docker-compose.yml'

  echo 'version: '"'"'2.2'"'" > docker-compose.yml
  echo 'services:' >> docker-compose.yml

  cat docker-compose-ingest.yml.in |
  sed -e "s/@PREFIX@/$prefix/g" \
  >> docker-compose.yml ||
  { echo 'Fail.' >&2; exit 1; }

  cat docker-compose-cassandra-1.yml.in \
  >> docker-compose.yml ||
  { echo 'Fail.' >&2; exit 1; }

  for i in `seq 2 "$storages"`; do
    cat docker-compose-cassandra-n.yml.in |
    sed -s "s,@CASSANDRA_ID@,$i,g" \
    >> docker-compose.yml ||
    { echo 'Fail.' >&2; exit 1; }
  done

  cat docker-compose-compute.yml.in \
  >> docker-compose.yml ||
  { echo 'Fail.' >&2; exit 1; }

  echo 'Done.'
  exit 0
}

echo 'Invalid config.' >&2
