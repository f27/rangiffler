package guru.qa.rangiffler.test.gql.mutation.user;

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

@Feature("user mutation")
@Story("UpdateUser")
@DisplayName("UpdateUser")
public class UpdateUserTest extends BaseGqlTest {

    @Test
    @ApiLogin
    @DisplayName("Информация о пользователе должна обновиться")
    void updateUserTest(@User UserModel user,
                        @Token String bearerToken,
                        @GqlRequestFile("gql/mutation/user/updateUser.json") GqlRequest request) throws IOException {
        final GqlUser gqlUser = gatewayApiClient.userMutation(bearerToken, request);

        step("Проверить ответ", () -> {
            step("Проверить, что id пользователя остался прежним", () ->
                    Assertions.assertEquals(user.id(), gqlUser.getData().getUser().getId()));
            step("Проверить, что имя обновилось", () ->
                    Assertions.assertEquals("updated firstname", gqlUser.getData().getUser().getFirstname()));
            step("Проверить, что фамилия обновилась", () ->
                    Assertions.assertEquals("updated lastname", gqlUser.getData().getUser().getSurname()));
            step("Проверить, что страна обновилась", () ->
                    Assertions.assertEquals("py", gqlUser.getData().getUser().getLocation().getCode()));
        });
    }
}
