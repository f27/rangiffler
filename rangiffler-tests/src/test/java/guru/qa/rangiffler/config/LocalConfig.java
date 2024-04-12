package guru.qa.rangiffler.config;

import com.codeborne.selenide.Configuration;
import guru.qa.rangiffler.model.grpc.GrpcAddress;

public class LocalConfig implements Config {

    static final LocalConfig instance = new LocalConfig();

    static {
        Configuration.baseUrl = instance.frontUrl();
        Configuration.browserSize = "1980x1024";
    }

    private LocalConfig() {
    }

    @Override
    public String frontUrl() {
        return "http://127.0.0.1:3001";
    }

    @Override
    public String authUrl() {
        return "http://127.0.0.1:9000";
    }

    @Override
    public String gatewayUrl() {
        return "http://127.0.0.1:8080";
    }

    @Override
    public GrpcAddress geoGrpcAddress() {
        return new GrpcAddress("127.0.0.1", 8092);
    }

    @Override
    public GrpcAddress photoGrpcAddress() {
        return new GrpcAddress("127.0.0.1", 8093);
    }

    @Override
    public GrpcAddress userdataGrpcAddress() {
        return new GrpcAddress("127.0.0.1", 8091);
    }

    @Override
    public String dbHost() {
        return "127.0.0.1";
    }

    @Override
    public String dbUser() {
        return "root";
    }

    @Override
    public String dbPassword() {
        return "secret";
    }

    @Override
    public String kafkaHost() {
        return "127.0.0.1";
    }

    @Override
    public String allureDockerUrl() {
        return "http://127.0.0.1:5050/";
    }
}
