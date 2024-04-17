package guru.qa.rangiffler.test.gql.query.user;

import guru.qa.grpc.rangiffler.grpc.FriendStatus;
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
@Story("GetOutcomeInvitations")
@DisplayName("GetOutcomeInvitations")
public class GetOutcomeInvitationsTest extends BaseGqlTest {

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.INVITATION_SENT)))
    @DisplayName("Должен вернуться пользователь от которого получено приглашение в друзья")
    void outcomeInvitationsShouldContainUserTest(@User(FOR_API_LOGIN) UserModel user,
                                                     @Token String bearerToken,
                                                     @GqlRequestFile("gql/query/user/getOutcomeInvitations.json") GqlRequest request) throws IOException {
        final GqlUser gqlUser = gatewayApiClient.userQuery(bearerToken, request);

        step("Проверить, что количество пользователей в ответе равно 1", () ->
                Assertions.assertEquals(1, gqlUser.getData().getUser().getOutcomeInvitations().getEdges().size()));
        step("Проверить, что этот пользователь ожидаемый", () ->
                Assertions.assertEquals(
                        user.friends().get(0).id(),
                        gqlUser.getData().getUser().getOutcomeInvitations().getEdges().get(0).getNode().getId()));
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend(status = FriendStatus.INVITATION_SENT)))
    @DisplayName("Нельзя рекурсивно запросить пользователей от которых получено приглашение в друзья")
    void getOutcomeInvitationsShouldReturnErrorIfRecursiveUserQueryTest(@Token String bearerToken,
                                                                          @GqlRequestFile("gql/query/user/getOutcomeInvitationsRecursive.json") GqlRequest request) throws IOException {
        final GqlUser gqlUser = gatewayApiClient.userQuery(bearerToken, request);

        step("Проверить, что data равна null", () ->
                Assertions.assertNull(gqlUser.getData()));
        step("Проверить, что вернулась ошибка", () ->
                Assertions.assertNotNull(gqlUser.getErrors()));
        step("Проверить, что количество ошибок 1", () ->
                Assertions.assertEquals(1, gqlUser.getErrors().size()));
        step("Проверить сообщение об ошибке", () ->
                Assertions.assertEquals(
                        "Can`t fetch over 1 outcomeInvitations sub-queries",
                        gqlUser.getErrors().get(0).message()));
    }
}
