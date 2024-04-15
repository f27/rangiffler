package guru.qa.rangiffler.config;

import com.codeborne.selenide.Configuration;
import guru.qa.rangiffler.model.grpc.GrpcAddress;
import org.openqa.selenium.chrome.ChromeOptions;

public class DockerConfig implements Config {

    static final DockerConfig instance = new DockerConfig();

    static {
        Configuration.baseUrl = instance.frontUrl();
        Configuration.remote = "http://selenoid:4444/wd/hub";
        Configuration.browser = "chrome";
        Configuration.browserVersion = "123.0";
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
    public GrpcAddress geoGrpcAddress() {
        return new GrpcAddress("geo.rangiffler.dc", 8092);
    }

    @Override
    public GrpcAddress photoGrpcAddress() {
        return new GrpcAddress("photo.rangiffler.dc", 8093);
    }

    @Override
    public GrpcAddress userdataGrpcAddress() {
        return new GrpcAddress("userdata.rangiffler.dc", 8091);
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

    @Override
    public String allureDockerUrl() {
        return System.getenv("ALLURE_DOCKER_API");
    }
}
