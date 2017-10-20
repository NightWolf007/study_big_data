#!/bin/bash

read -r -d '' code << 'EOF'

function array-random() {
  array=("$@")
  end=$((${#array[@]} - 1))
  index=`shuf -i 0-$end -n 1`
  echo ${array[$index]}
}

function random-ip() {
  shuf -i 0-255 -n 4 | xargs | sed 's/ /./g'
}

function random-date() {
  shuf -i 1400000000-1500000000 -n 1 | sed 's/^/@/' | xargs date +'%d/%b/%Y:%H:%M:%S +0300' -d
}

function random-path() {
  words=(`ls /etc`)
  path=
  for i in `seq 0 \`shuf -i 0-4 -n 1\``; do
    path="$path/`array-random "${words[@]}"`"
  done
  [ -z "$path" ] && echo / || echo "$path"
}

function random-ua() {
  brackets=(
    'Windows NT 6.1'
    'Macintosh; Intel Mac OS X 10_10_1'
    'X11; Linux x86_64'
    'Windows NT 6.1; WOW64'
    'Windows NT 6.3; WOW64'
    'Windows NT 6.4; WOW64'
    'Windows NT 5.1'
    'Windows NT 10.0'
    'Macintosh; Intel Mac OS X 10_10_1'
    'Windows NT 6.3; Win64; x64'
    'Windows NT 4.0; WOW64'
  )

  suffixes=(
    'Gecko/20100101 Firefox/24.0'
    'Gecko/20100101 Firefox/25.0'
    'Gecko/20100101 Firefox/28.0'
    'Gecko/20100101 Firefox/29.0'
    'Gecko/20100101 Firefox/31.0'
    'Gecko/20100101 Firefox/33.0'
    'Gecko/20100101 Firefox/36.0'
    'Gecko/20100101 Firefox/40.1'
    'Gecko/20120101 Firefox/29.0'
    'Gecko/20121011 Firefox/27.0'
    'Gecko/20130101 Firefox/27.3'
    'Gecko/20130401 Firefox/31.0'
    'AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36'
    'AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.124 Safari/537.36'
    'AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36'
    'AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2224.3 Safari/537.36'
    'AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2225.0 Safari/537.36'
    'AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2226.0 Safari/537.36'
    'AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.0 Safari/537.36'
    'AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.1 Safari/537.36'
    'AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36'
    'AppleWebKit/533.20.25 (KHTML, like Gecko) Version/5.0.4 Safari/533.20.27'
    'AppleWebKit/533.21.1 (KHTML, like Gecko) Version/5.0.5 Safari/533.21.1'
    'AppleWebKit/534.46 (KHTML, like Gecko ) Version/5.1 Mobile/9B176 Safari/7534.48.3'
    'AppleWebKit/534.55.3 (KHTML, like Gecko) Version/5.1.3 Safari/534.53.10'
    'AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5355d Safari/8536.25'
    'AppleWebKit/537.13+ (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2'
    'AppleWebKit/537.75.14 (KHTML, like Gecko) Version/7.0.3 Safari/7046A194A'
    'AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.246'
    'Gecko/20100101 Thunderbird/38.2.0 Lightning/4.0.2'
    'Gecko/20100101 Thunderbird/38.1.0 Lightning/4.0.2'
  )

  echo "Mozilla/5.0 (`array-random "${brackets[@]}"`) `array-random "${suffixes[@]}"`"
}

function random-log() {
  ip=`random-ip`
  date=`random-date`
  path=`random-path`
  payload=`shuf -i 0-100000 -n 1`
  referer=`[ "\`shuf -i 0-1 -n 1\`" -eq 0 ] && echo "http://host"\`random-path\` || echo -`
  ua=`random-ua`
  echo "$ip - - [$date] \"GET $path HTTP/1.1\" 200 $payload \"$referer\" \"$ua\""
}

[ "$1" == '--no-gen' ] || {
  rm -rf input
  mkdir input

  for i in `seq 1 \`shuf -i 3-5 -n 1\``; do
    input="input/input$i.log"
    echo "==> Generating input$i.log..."
    echo -n > "$input"
    for j in `seq 1 \`shuf -i 100-500 -n 1\``; do
      random-log >> "$input"
    done
  done
}

echo "==> Starting task..."
hadoop fs -rm -r -f input output
hadoop fs -mkdir -p input
hadoop fs -put input/* input
hadoop fs -ls input
hadoop jar /opt/mapreduce*.jar input output
echo "==> Pulling result..."
hadoop fs -cat output/part-r-00000
echo
hadoop fs -text output/part-r-00000

EOF

sudo docker cp ../mapreduce/target/scala-2.12/mapreduce*.jar hadoop-master:/opt/ || exit $?
sudo docker exec -ti hadoop-master /bin/bash -c "$code" bash "$@" || exit $?
