package guru.qa.rangiffler.test.gql.query.users;

import guru.qa.grpc.rangiffler.grpc.FriendStatus;
import guru.qa.rangiffler.jupiter.annotation.*;
import guru.qa.rangiffler.model.gql.GqlRequest;
import guru.qa.rangiffler.model.gql.response.GqlUsers;
import guru.qa.rangiffler.test.gql.BaseGqlTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.qameta.allure.Allure.step;

@Feature("users")
@Story("GetPeople")
@DisplayName("GetPeople")
public class GetPeopleTest extends BaseGqlTest {

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.NOT_FRIEND)))
    @DisplayName("Должен вернуться хотя бы один пользователь")
    void peopleShouldContainAtLeastOneUserTest(@Token String bearerToken,
                                                  @GqlRequestFile("gql/query/users/getPeopleQuery.json") GqlRequest request) throws IOException {
        final GqlUsers gqlUsers = gatewayApiClient.getPeople(bearerToken, request);
        step("Проверить, что количество пользователей в ответе не равно 0", () ->
                Assertions.assertNotEquals(0, gqlUsers.getData().getUsers().getEdges().size()));
    }

}
