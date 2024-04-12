ARG DOCKER
FROM $DOCKER

WORKDIR /rangiffler
COPY ./gradle ./gradle
COPY ./gradlew ./
COPY ./rangiffler-tests ./rangiffler-tests
COPY ./rangiffler-grpc-common ./rangiffler-grpc-common
COPY ./build.gradle ./
COPY ./settings.gradle ./

RUN sed -i 's/\r$//' ./gradlew
RUN chmod +x ./gradlew

CMD ./gradlew test

