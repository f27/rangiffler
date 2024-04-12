#!/bin/bash
source ./docker.properties
export PROFILE=docker

echo '### Java version ###'
java --version

front_path="./rangiffler-gql-client"
front_image="${IMAGE_PREFIX}/${FRONT_IMAGE_NAME}-${PROFILE}"
ARCH=$(uname -m)

echo '### Docker-compose down ###'
FRONT_IMAGE="${IMAGE_PREFIX}/${FRONT_IMAGE_NAME_GQL}-${PROFILE}:latest" PREFIX="${IMAGE_PREFIX}" PROFILE="${PROFILE}" docker-compose down

docker_containers="$(docker ps -a -q)"
docker_images="$(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'rangiffler')"

if [ ! -z "$docker_containers" ]; then
  echo "### Stop containers: $docker_containers ###"
  docker stop $(docker ps -a -q)
  docker rm $(docker ps -a -q)
fi
if [ ! -z "$docker_images" ]; then
  echo "### Remove images: $docker_images ###"
  docker rmi $(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'rangiffler')
fi

docker volume rm rangiffler-allure-artifacts

echo "### Build backend ###"
bash ./gradlew jibDockerBuild -x :rangiffler-tests:test

echo "### Build frontend (front_path: $front_path) ###"
docker build -t ${IMAGE_PREFIX}/${FRONT_IMAGE_NAME}-${PROFILE}:${FRONT_VERSION} -t ${IMAGE_PREFIX}/${FRONT_IMAGE_NAME}-${PROFILE}:latest "$front_path"

echo "### Build tests ###"
if [ "$ARCH" = "arm64" ] || [ "$ARCH" = "aarch64" ]; then
  docker_arch="linux/arm64"
  docker build --build-arg DOCKER=arm64v8/eclipse-temurin:19-jdk -t "${IMAGE_PREFIX}/${TEST_IMAGE_NAME}:latest" -f ./rangiffler-tests/Dockerfile .
else
  docker_arch="linux/amd64"
  docker build --build-arg DOCKER=eclipse-temurin:19-jdk -t "${IMAGE_PREFIX}/${TEST_IMAGE_NAME}:latest" -f ./rangiffler-tests/Dockerfile .
fi

echo "### Pull selenoid chrome ###"
docker pull selenoid/vnc_chrome:123.0
docker images
ARCH="$docker_arch" FRONT_IMAGE="$front_image" PREFIX="${IMAGE_PREFIX}" PROFILE="${PROFILE}" docker-compose -f docker-compose-test.yml up -d
docker ps -a