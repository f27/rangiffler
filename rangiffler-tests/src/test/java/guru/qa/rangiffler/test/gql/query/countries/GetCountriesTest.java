package guru.qa.rangiffler.test.gql.query.countries;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.GqlRequestFile;
import guru.qa.rangiffler.jupiter.annotation.Token;
import guru.qa.rangiffler.model.gql.GqlRequest;
import guru.qa.rangiffler.model.gql.response.GqlCountries;
import guru.qa.rangiffler.test.gql.BaseGqlTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.qameta.allure.Allure.step;

@Feature("countries query")
@Story("GetCountries")
@DisplayName("GetCountries")
public class GetCountriesTest extends BaseGqlTest {

    @Test
    @ApiLogin
    @DisplayName("Должен возвращаться список стран")
    void getCountriesTest(@Token String bearerToken,
                          @GqlRequestFile("gql/query/countries/getCountries.json") GqlRequest request) throws IOException {
        final GqlCountries gqlCountries = gatewayApiClient.countriesQuery(bearerToken, request);

        step("Проверить, что в ответе 238 стран", () ->
                Assertions.assertEquals(238, gqlCountries.getData().getCountries().size()));
    }
}
