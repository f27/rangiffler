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
                                               @GqlRequestFile("gql/query/users/getPeople.json") GqlRequest request) throws IOException {
        final GqlUsers gqlUsers = gatewayApiClient.usersQuery(bearerToken, request);
        step("Проверить, что количество пользователей в ответе не равно 0", () ->
                Assertions.assertNotEquals(0, gqlUsers.getData().getUsers().getEdges().size()));
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend))
    @DisplayName("Должна вернуться ошибка если запрашиваем у пользователей друзей")
    void getPeopleShouldReturnErrorIfRequestingFriendsTest(@Token String bearerToken,
                                               @GqlRequestFile("gql/query/users/getPeopleWithFriends.json") GqlRequest request) throws IOException {
        final GqlUsers gqlUsers = gatewayApiClient.usersQuery(bearerToken, request);

        step("Проверить, что data равна null", () ->
                Assertions.assertNull(gqlUsers.getData().getUsers()));
        step("Проверить, что вернулась ошибка", () ->
                Assertions.assertNotNull(gqlUsers.getErrors()));
        step("Проверить, что количество ошибок 1", () ->
                Assertions.assertEquals(1, gqlUsers.getErrors().size()));
        step("Проверить сообщение об ошибке", () ->
                Assertions.assertEquals(
                        "Can`t fetch over 0 friends sub-queries",
                        gqlUsers.getErrors().get(0).message()));
    }

    @Test
    @ApiLogin
    @DisplayName("Должна вернуться ошибка если запрашиваем у пользователей полученные приглашения")
    void getPeopleShouldReturnErrorIfRequestingIncomeInvitationsTest(@Token String bearerToken,
                                                           @GqlRequestFile("gql/query/users/getPeopleWithIncomeInvitations.json") GqlRequest request) throws IOException {
        final GqlUsers gqlUsers = gatewayApiClient.usersQuery(bearerToken, request);

        step("Проверить, что data равна null", () ->
                Assertions.assertNull(gqlUsers.getData().getUsers()));
        step("Проверить, что вернулась ошибка", () ->
                Assertions.assertNotNull(gqlUsers.getErrors()));
        step("Проверить, что количество ошибок 1", () ->
                Assertions.assertEquals(1, gqlUsers.getErrors().size()));
        step("Проверить сообщение об ошибке", () ->
                Assertions.assertEquals(
                        "Can`t fetch over 0 incomeInvitations sub-queries",
                        gqlUsers.getErrors().get(0).message()));
    }

    @Test
    @ApiLogin
    @DisplayName("Должна вернуться ошибка если запрашиваем у пользователей отправленные приглашения")
    void getPeopleShouldReturnErrorIfRequestingOutcomeInvitationsTest(@Token String bearerToken,
                                                                     @GqlRequestFile("gql/query/users/getPeopleWithOutcomeInvitations.json") GqlRequest request) throws IOException {
        final GqlUsers gqlUsers = gatewayApiClient.usersQuery(bearerToken, request);

        step("Проверить, что data равна null", () ->
                Assertions.assertNull(gqlUsers.getData().getUsers()));
        step("Проверить, что вернулась ошибка", () ->
                Assertions.assertNotNull(gqlUsers.getErrors()));
        step("Проверить, что количество ошибок 1", () ->
                Assertions.assertEquals(1, gqlUsers.getErrors().size()));
        step("Проверить сообщение об ошибке", () ->
                Assertions.assertEquals(
                        "Can`t fetch over 0 outcomeInvitations sub-queries",
                        gqlUsers.getErrors().get(0).message()));
    }
}
