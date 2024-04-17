package guru.qa.rangiffler.test.gql.query.user;

import guru.qa.rangiffler.jupiter.annotation.*;
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

import static guru.qa.rangiffler.jupiter.annotation.User.GenerationType.FOR_API_LOGIN;
import static io.qameta.allure.Allure.step;

@Feature("user query")
@Story("GetFriends")
@DisplayName("GetFriends")
public class GetFriendsTest extends BaseGqlTest {

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend))
    @DisplayName("Должен вернуться друг")
    void friendsShouldContainOurFriendTest(@User(FOR_API_LOGIN) UserModel user,
                                           @Token String bearerToken,
                                           @GqlRequestFile("gql/query/user/getFriends.json") GqlRequest request) throws IOException {
        final GqlUser gqlUser = gatewayApiClient.userQuery(bearerToken, request);

        step("Проверить, что количество друзей в ответе равно 1", () ->
                Assertions.assertEquals(1, gqlUser.getData().getUser().getFriends().getEdges().size()));
        step("Проверить, что этот друг ожидаемый", () ->
                Assertions.assertEquals(
                        user.friends().get(0).id(),
                        gqlUser.getData().getUser().getFriends().getEdges().get(0).getNode().getId()));
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend))
    @DisplayName("Нельзя рекурсивно запросить друзей")
    void getFriendsShouldReturnErrorIfRecursiveFriendsQueryTest(@Token String bearerToken,
                                                             @GqlRequestFile("gql/query/user/getFriendsRecursive.json") GqlRequest request) throws IOException {
        final GqlUser gqlUser = gatewayApiClient.userQuery(bearerToken, request);

        step("Проверить, что data равна null", () ->
                Assertions.assertNull(gqlUser.getData()));
        step("Проверить сообщение об ошибке", () ->
                Assertions.assertEquals(
                        "Can`t fetch over 1 friends sub-queries",
                        gqlUser.getErrors().get(0).message()));
    }
}
