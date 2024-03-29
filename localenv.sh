#!/bin/bash

echo '### Stop and remove containers ###'
docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)

echo '### Pull docker images ###'
docker pull mysql:8.3.0
docker pull confluentinc/cp-zookeeper:7.3.2
docker pull confluentinc/cp-kafka:7.3.2

echo '### Run databases ###'
docker run --name rangiffler-all-db -p 3306:3306 -e MYSQL_ROOT_PASSWORD=secret -v rangiffler-all-db:/var/lib/mysql -d mysql:8.3.0

echo '### Run kafka ###'
docker run --name=zookeeper -e ZOOKEEPER_CLIENT_PORT=2181 -e ZOOKEEPER_TICK_TIME=2000 -p 2181:2181 -d confluentinc/cp-zookeeper:7.3.2
docker run --name=kafka -e KAFKA_BROKER_ID=1 \
-e KAFKA_ZOOKEEPER_CONNECT=$(docker inspect zookeeper --format='{{ .NetworkSettings.IPAddress }}'):2181 \
-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
-e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
-e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
-e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
-p 9092:9092 -d confluentinc/cp-kafka:7.3.2

echo '### Run client ###'
cd rangiffler-gql-client
npm i
npm run dev