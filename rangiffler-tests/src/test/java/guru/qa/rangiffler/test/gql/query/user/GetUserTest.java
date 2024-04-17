package guru.qa.rangiffler.test.gql.query.user;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.GqlRequestFile;
import guru.qa.rangiffler.jupiter.annotation.Token;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.model.gql.GqlRequest;
import guru.qa.rangiffler.model.gql.response.GqlUser;
import guru.qa.rangiffler.test.gql.BaseGqlTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.qameta.allure.Allure.step;

@Feature("user query")
@Story("GetUser")
@DisplayName("GetUser")
public class GetUserTest extends BaseGqlTest {

    @Test
    @ApiLogin
    @DisplayName("Должен вернуться текущий пользователь")
    void userShouldBeReturnedTest(@User UserModel user,
                                  @Token String bearerToken,
                                  @GqlRequestFile("gql/query/user/getUserQuery.json") GqlRequest request) throws IOException {
        final GqlUser gqlUser = gatewayApiClient.getUser(bearerToken, request);
        step("Проверить username", () ->
                Assertions.assertEquals(user.username(), gqlUser.getData().getUser().getUsername()));
    }
}
