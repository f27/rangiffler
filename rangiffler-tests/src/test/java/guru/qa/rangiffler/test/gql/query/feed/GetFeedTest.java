package guru.qa.rangiffler.test.gql.query.feed;

import guru.qa.rangiffler.jupiter.annotation.*;
import guru.qa.rangiffler.model.gql.GqlRequest;
import guru.qa.rangiffler.model.gql.response.GqlFeed;
import guru.qa.rangiffler.test.gql.BaseGqlTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.qameta.allure.Allure.step;

@Feature("feed query")
@Story("GetFeed")
@DisplayName("GetFeed")
public class GetFeedTest extends BaseGqlTest {

    @Test
    @ApiLogin(
            user = @GenerateUser(
                    photos = @Photo,
                    friends = @Friend(
                            photos = @Photo
                    )
            )
    )
    @DisplayName("GetFeed без друзей: должна возвращаться лента только с моими фотографиями")
    void getFeedWithoutFriendsTest(@Token String bearerToken,
                                   @GqlRequestFile("gql/query/feed/getFeedWithoutFriends.json") GqlRequest request) throws IOException {
        final GqlFeed gqlFeed = gatewayApiClient.feedQuery(bearerToken, request);
        step("Проверить, что в ответе 1 фотография", () ->
                Assertions.assertEquals(1, gqlFeed.getData().getFeed().getPhotos().getEdges().size()));
    }

    @Test
    @ApiLogin(
            user = @GenerateUser(
                    photos = @Photo,
                    friends = @Friend(
                            photos = @Photo
                    )
            )
    )
    @DisplayName("GetFeed с друзьями: должна возвращаться лента с моими фотографиями и фотографиями друзей")
    void getFeedWithFriendsTest(@Token String bearerToken,
                                   @GqlRequestFile("gql/query/feed/getFeedWithFriends.json") GqlRequest request) throws IOException {
        final GqlFeed gqlFeed = gatewayApiClient.feedQuery(bearerToken, request);
        step("Проверить, что в ответе 2 фотографии", () ->
                Assertions.assertEquals(2, gqlFeed.getData().getFeed().getPhotos().getEdges().size()));
    }
}
