package guru.qa.rangiffler.config;

import java.util.List;

public interface Config {

    static Config getInstance() {
        return "docker".equals(System.getProperty("test.env"))
                ? DockerConfig.instance
                : LocalConfig.instance;
    }

    String frontUrl();

    String authUrl();

    String gatewayUrl();

    String geoGrpcHost();

    default int geoGrpcPort() {
        return 8092;
    }

    String photoGrpcHost();

    default int photoGrpcPort() {
        return 8093;
    }

    String userdataGrpcHost();

    default int userdataGrpcPort() {
        return 8091;
    }

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
}
