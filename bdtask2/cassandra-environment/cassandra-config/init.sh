#!/bin/bash

[ "$1" = 'master' ] && {
  seeds=`hostname --ip`
  sucmd='(until cqlsh -f /host/create.cql; do sleep 2; done) & exec /usr/sbin/cassandra -f'
} || {
  seeds='cassandra1'
  sucmd='exec /usr/sbin/cassandra -f'
}

mkdir /home/cassandra
chown `id -un 999`:`id -gn 999` /home/cassandra
sed -i /etc/cassandra/cassandra.yaml \
-e "s/- seeds: .*/- seeds: \"$seeds\"/" \
-e "s/listen_address: .*/listen_address: `hostname --ip`\nbroadcast_address: `hostname --ip`/" \
-e "s/rpc_address: .*/rpc_address: 0.0.0.0\nbroadcast_rpc_address: `hostname --ip`/"

exec su `id -un 999` -c "$sucmd"
