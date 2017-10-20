#!/bin/bash

slaves_count="${1:-0}"

compose_file='docker-compose.yml'

echo 'version: '"'"'3'"'" > "$compose_file"
echo 'services:' >> "$compose_file"

cat 'name-node.yml.in' |
sed 's/@SLAVES_COUNT@/'"$slaves_count"'/g' \
>> "$compose_file"

for i in `seq 1 "${1:-0}"`; do
	cat 'data-node.yml.in' |
	sed 's/@SLAVES_COUNT@/'"$slaves_count"'/g' |
	sed 's/@INDEX@/'"$i"'/g' \
	>> "$compose_file"
done
