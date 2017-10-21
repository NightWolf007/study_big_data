#!/bin/bash

[ -d 'hadoop-cluster-docker' ] ||
git clone https://github.com/kiwenlau/hadoop-cluster-docker ||
exit 1

(cd 'hadoop-cluster-docker' && git reset --hard origin/master && git checkout master) || exit 1

sed -e 's/14.04/16.04/' \
-e 's/openjdk-7-jdk/openjdk-8-jdk/' \
-e 's/java-7/java-8/' \
-e 's|https://.*hadoop-2.7.2.tar.gz|https://archive.apache.org/dist/hadoop/core/hadoop-2.7.2/hadoop-2.7.2.tar.gz|' \
-e 's/\(apt-get install .*\)\(\\\)\?$/\1 libsnappy-dev\2/' \
-i 'hadoop-cluster-docker/Dockerfile' || exit 1

sed -e 's/java-7/java-8/' \
-i 'hadoop-cluster-docker/config/hadoop-env.sh' || exit 1

perl -0777 -pe 's|
</configuration>|
    <property>
        <name>yarn.nodemanager.disk-health-checker.max-disk-utilization-per-disk-percentage</name>
        <value>100</value>
    </property>
</configuration>|' \
-i "hadoop-cluster-docker/config/yarn-site.xml" || exit $?

(cd hadoop-cluster-docker && sudo docker build -t kiwenlau/hadoop:1.0 "$@" .) || exit 1
rm -rf 'hadoop-cluster-docker' || exit 1
