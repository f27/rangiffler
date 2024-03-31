package guru.qa.rangiffler.config;

import com.codeborne.selenide.Configuration;

public class LocalConfig implements Config {

    private LocalConfig() {}

    static final LocalConfig instance = new LocalConfig();

    static {
        Configuration.browserSize = "1980x1024";
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
    public String geoGrpcHost() {
        return "127.0.0.1";
    }

    @Override
    public String photoGrpcHost() {
        return "127.0.0.1";
    }

    @Override
    public String userdataGrpcHost() {
        return "127.0.0.1";
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
}
