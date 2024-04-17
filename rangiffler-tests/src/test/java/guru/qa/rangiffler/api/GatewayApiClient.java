package guru.qa.rangiffler.api;

import guru.qa.rangiffler.model.gql.GqlRequest;
import guru.qa.rangiffler.model.gql.response.*;
import io.qameta.allure.Step;

import java.io.IOException;

public class GatewayApiClient extends RestClient {

    private final GatewayApi gatewayApi;

    public GatewayApiClient() {
        super(CFG.gatewayUrl());
        gatewayApi = retrofit.create(GatewayApi.class);
    }

    @Step("Получить информацию о пользователе")
    public GqlUser userQuery(String bearerToken, GqlRequest request) throws IOException {
        return gatewayApi.userQuery(bearerToken, request).execute().body();
    }

    @Step("Получить список пользователей")
    public GqlUsers usersQuery(String bearerToken, GqlRequest request) throws IOException {
        return gatewayApi.usersQuery(bearerToken, request).execute().body();
    }

    @Step("Получить список стран")
    public GqlCountries countriesQuery(String bearerToken, GqlRequest request) throws IOException {
        return gatewayApi.countriesQuery(bearerToken, request).execute().body();
    }

    @Step("Получить ленту")
    public GqlFeed feedQuery(String bearerToken, GqlRequest request) throws IOException {
        return gatewayApi.feedQuery(bearerToken, request).execute().body();
    }

    @Step("Обновить информацию о пользователе")
    public GqlUser userMutation(String bearerToken, GqlRequest request) throws IOException {
        return gatewayApi.userMutation(bearerToken, request).execute().body();
    }

    @Step("Создать/изменить фотографию")
    public GqlPhoto photoMutation(String bearerToken, GqlRequest request) throws IOException {
        return gatewayApi.photoMutation(bearerToken, request).execute().body();
    }
}
