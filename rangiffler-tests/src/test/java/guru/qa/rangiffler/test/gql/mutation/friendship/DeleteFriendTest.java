package guru.qa.rangiffler.test.gql.mutation.friendship;

import guru.qa.rangiffler.jupiter.annotation.*;
import guru.qa.rangiffler.model.FriendshipAction;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.model.gql.GqlRequest;
import guru.qa.rangiffler.model.gql.response.GqlFriendship;
import guru.qa.rangiffler.test.gql.BaseGqlTest;
import guru.qa.rangiffler.util.GqlVariablesUtil;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static io.qameta.allure.Allure.step;

@Feature("friendship mutation")
@Story("DeleteFriend")
@DisplayName("DeleteFriend")
public class DeleteFriendTest extends BaseGqlTest {

    @Test
    @ApiLogin(user = @GenerateUser(friends = @Friend))
    @DisplayName("DeleteFriend: можно удалить друга")
    void deleteFriendTest(@User UserModel user,
                          @Token String bearerToken,
                          @GqlRequestFile("gql/mutation/friendship/updateFriendship.json") GqlRequest template) throws IOException {
        final UUID friendId = user.friends().get(0).id();
        GqlRequest request = new GqlRequest(
                template.operationName(),
                GqlVariablesUtil.updateFriendship(friendId, FriendshipAction.DELETE),
                template.query()
        );

        final GqlFriendship gqlResponse = gatewayApiClient.updateFriendshipMutation(bearerToken, request);

        step("Проверить id друга", () ->
                Assertions.assertEquals(friendId, gqlResponse.getData().getFriendship().getId()));
        step("Проверить, что нет статуса дружбы", () ->
                Assertions.assertNull(gqlResponse.getData().getFriendship().getFriendStatus()));
    }
}
