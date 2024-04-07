package guru.qa.rangiffler.config;

import com.codeborne.selenide.Configuration;
import org.openqa.selenium.chrome.ChromeOptions;

public class DockerConfig implements Config {

    static final DockerConfig instance = new DockerConfig();

    static {
        Configuration.baseUrl = instance.frontUrl();
        Configuration.remote = "http://selenoid:4444/wd/hub";
        Configuration.browser = "chrome";
        Configuration.browserVersion = "117.0";
        Configuration.browserCapabilities = new ChromeOptions().addArguments("--no-sandbox");
        Configuration.browserSize = "1980x1024";
    }

    private DockerConfig() {
    }

    @Override
    public String frontUrl() {
        return "http://frontend.rangiffler.dc";
    }

    @Override
    public String authUrl() {
        return "http://auth.rangiffler.dc:9000";
    }

    @Override
    public String gatewayUrl() {
        return "http://gateway.rangiffler.dc:8080";
    }

    @Override
    public String geoGrpcHost() {
        return "geo.rangiffler.dc";
    }

    @Override
    public String photoGrpcHost() {
        return "photo.rangiffler.dc";
    }

    @Override
    public String userdataGrpcHost() {
        return "userdata.rangiffler.dc";
    }

    @Override
    public String dbHost() {
        return "rangiffler-mysql";
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
        return "kafka";
    }
}
