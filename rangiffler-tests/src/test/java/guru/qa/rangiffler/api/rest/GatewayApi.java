package guru.qa.rangiffler.api.rest;

import guru.qa.rangiffler.model.gql.GqlRequest;
import guru.qa.rangiffler.model.gql.response.GqlUser;
import guru.qa.rangiffler.model.gql.response.GqlUsers;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GatewayApi {

    @POST("/graphql")
    Call<Void> createPhoto(@Header("Authorization") String bearerToken,
                           @Body GqlRequest gqlRequest);

    @POST("/graphql")
    Call<Void> deletePhoto(@Header("Authorization") String bearerToken,
                           @Body GqlRequest gqlRequest);

    @POST("/graphql")
    Call<Void> getCountries(@Header("Authorization") String bearerToken,
                            @Body GqlRequest gqlRequest);

    @POST("/graphql")
    Call<Void> getFeed(@Header("Authorization") String bearerToken,
                       @Body GqlRequest gqlRequest);

    @POST("/graphql")
    Call<GqlUser> getFriends(@Header("Authorization") String bearerToken,
                          @Body GqlRequest gqlRequest);

    @POST("/graphql")
    Call<Void> getIncomeInvitations(@Header("Authorization") String bearerToken,
                                    @Body GqlRequest gqlRequest);

    @POST("/graphql")
    Call<Void> getOutcomeInvitations(@Header("Authorization") String bearerToken,
                                     @Body GqlRequest gqlRequest);

    @POST("/graphql")
    Call<GqlUser> getUser(@Header("Authorization") String bearerToken,
                          @Body GqlRequest gqlRequest);

    @POST("/graphql")
    Call<Void> likePhoto(@Header("Authorization") String bearerToken,
                         @Body GqlRequest gqlRequest);

    @POST("/graphql")
    Call<GqlUsers> getPeople(@Header("Authorization") String bearerToken,
                             @Body GqlRequest gqlRequest);

    @POST("/graphql")
    Call<Void> updateFriendshipStatus(@Header("Authorization") String bearerToken,
                                      @Body GqlRequest gqlRequest);

    @POST("/graphql")
    Call<Void> updatePhoto(@Header("Authorization") String bearerToken,
                           @Body GqlRequest gqlRequest);

    @POST("/graphql")
    Call<Void> updateUser(@Header("Authorization") String bearerToken,
                          @Body GqlRequest gqlRequest);
}
