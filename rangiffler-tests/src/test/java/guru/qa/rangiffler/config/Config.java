package guru.qa.rangiffler.config;

import guru.qa.rangiffler.model.grpc.GrpcAddress;

import java.util.List;

public interface Config {

    static Config getInstance() {
        return "docker".equals(System.getenv("TEST_ENV"))
                ? DockerConfig.instance
                : LocalConfig.instance;
    }

    String frontUrl();

    String authUrl();

    String gatewayUrl();

    GrpcAddress geoGrpcAddress();

    GrpcAddress photoGrpcAddress();

    GrpcAddress userdataGrpcAddress();

    String dbHost();

    default int dbPort() {
        return 3306;
    }

    String dbUser();

    String dbPassword();

    String kafkaHost();

    default int kafkaPort() {
        return 9092;
    }

    default List<String> kafkaTopics() {
        return List.of("users");
    }

    String allureDockerUrl();
}
