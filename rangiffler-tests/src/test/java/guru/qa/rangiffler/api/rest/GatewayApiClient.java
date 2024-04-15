package guru.qa.rangiffler.api.rest;

import guru.qa.rangiffler.model.gql.GqlRequest;
import guru.qa.rangiffler.model.gql.response.GqlUser;
import guru.qa.rangiffler.model.gql.response.GqlUsers;
import io.qameta.allure.Step;

import java.io.IOException;

public class GatewayApiClient extends RestClient {

    private final GatewayApi gatewayApi;

    public GatewayApiClient() {
        super(CFG.gatewayUrl());
        gatewayApi = retrofit.create(GatewayApi.class);
    }

    @Step("Получить информацию о пользователе")
    public GqlUser getUser(String bearerToken, GqlRequest request) throws IOException {
        return gatewayApi.getUser(bearerToken, request).execute().body();
    }

    @Step("Получить информацию список людей")
    public GqlUsers getPeople(String bearerToken, GqlRequest request) throws IOException {
        return gatewayApi.getPeople(bearerToken, request).execute().body();
    }
}
