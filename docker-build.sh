#!/bin/bash
source ./docker.properties
export PROFILE="${PROFILE:=docker}"

echo '### Java version ###'
java --version

front_path="./rangiffler-gql-client"
front_image="${IMAGE_PREFIX}/${FRONT_IMAGE_NAME}-${PROFILE}"

echo '### Docker-compose down ###'
FRONT_IMAGE="${IMAGE_PREFIX}/${FRONT_IMAGE_NAME_GQL}-${PROFILE}:latest" PREFIX="${IMAGE_PREFIX}" PROFILE="${PROFILE}" docker-compose down

echo '### Java version ###'
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

if [ "$1" = "push" ]; then
  echo "### Build & push images (front_path: $front_path) ###"
  bash ./gradlew jib
  docker build -t ${IMAGE_PREFIX}/${FRONT_IMAGE_NAME}-${PROFILE}:${FRONT_VERSION} -t ${IMAGE_PREFIX}/${FRONT_IMAGE_NAME}-${PROFILE}:latest "$front_path"
else
  echo "### Build images (front_path: $front_path) ###"
  bash ./gradlew jibDockerBuild
  docker build -t ${IMAGE_PREFIX}/${FRONT_IMAGE_NAME}-${PROFILE}:${FRONT_VERSION} -t ${IMAGE_PREFIX}/${FRONT_IMAGE_NAME}-${PROFILE}:latest "$front_path"
fi

echo "### Images: ###"
docker images

echo "### Docker-compose up -d: ###"
FRONT_IMAGE="$front_image" PREFIX="${IMAGE_PREFIX}" PROFILE="${PROFILE}" docker-compose up -d

docker ps -a