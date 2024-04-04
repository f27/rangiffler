package guru.qa.rangiffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.rangiffler.api.interceptor.CodeInterceptor;
import guru.qa.rangiffler.jupiter.extension.ApiLoginExtension;
import io.qameta.allure.Step;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public class AuthApiClient extends RestClient {

    private final AuthApi authApi;

    public AuthApiClient() {
        super(
                CFG.authUrl(),
                true,
                JacksonConverterFactory.create(),
                new CodeInterceptor()
        );
        authApi = retrofit.create(AuthApi.class);
    }

    @Step("Авторизоваться")
    public void doLogin(String username, String password) throws IOException {
        authApi.authorize(
                "code",
                "client",
                "openid",
                CFG.frontUrl() + "/authorized",
                ApiLoginExtension.getCodeChallenge(),
                "S256"
        ).execute();

        authApi.login(
                username,
                password,
                ApiLoginExtension.getCsrfToken()
        ).execute();

        JsonNode responseBody = authApi.token(
                "Basic " + new String(Base64.getEncoder().encode("client:secret".getBytes(StandardCharsets.UTF_8))),
                "client",
                CFG.frontUrl() + "/authorized",
                "authorization_code",
                ApiLoginExtension.getCode(),
                ApiLoginExtension.getCodeVerifier()
        ).execute().body();

        final String token = Objects.requireNonNull(responseBody).get("id_token").asText();
        ApiLoginExtension.setToken(token);
    }
}
