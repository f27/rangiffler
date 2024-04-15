#!/bin/bash
source ./docker.properties
export PROFILE=${PROFILE}

echo '### Java version ###'
java --version

front_path="./rangiffler-gql-client"
front_image="${IMAGE_PREFIX}/${FRONT_IMAGE_NAME}-${PROFILE}"

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

if [ "$1" = "push" ]; then
  echo "### Build & push backend ###"
  bash ./gradlew jib -x :rangiffler-tests:test
  docker build -t ${IMAGE_PREFIX}/${FRONT_IMAGE_NAME}-${PROFILE}:${FRONT_VERSION} -t ${IMAGE_PREFIX}/${FRONT_IMAGE_NAME}-${PROFILE}:latest "$front_path"
  echo "### Build & push frontend (front_path: $front_path) ###"
  docker push ${IMAGE_PREFIX}/${FRONT_IMAGE_NAME}-${PROFILE}:${FRONT_VERSION}
  docker push ${IMAGE_PREFIX}/${FRONT_IMAGE_NAME}-${PROFILE}:latest
else
  echo "### Build backend ###"
  bash ./gradlew jibDockerBuild -x :rangiffler-tests:test
  echo "### Build frontend (front_path: $front_path) ###"
  docker build -t ${IMAGE_PREFIX}/${FRONT_IMAGE_NAME}-${PROFILE}:${FRONT_VERSION} -t ${IMAGE_PREFIX}/${FRONT_IMAGE_NAME}-${PROFILE}:latest "$front_path"
fi

echo "### Images: ###"
docker images

echo "### Docker-compose up -d: ###"
FRONT_IMAGE="$front_image" PREFIX="${IMAGE_PREFIX}" PROFILE="${PROFILE}" docker-compose up -d

docker ps -a