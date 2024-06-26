package guru.qa.rangiffler.api;

import guru.qa.rangiffler.model.gql.GqlRequest;
import guru.qa.rangiffler.model.gql.response.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GatewayApi {

    @POST("/graphql")
    Call<GqlUser> userQuery(@Header("Authorization") String bearerToken,
                            @Body GqlRequest gqlRequest);

    @POST("/graphql")
    Call<GqlUsers> usersQuery(@Header("Authorization") String bearerToken,
                              @Body GqlRequest gqlRequest);

    @POST("/graphql")
    Call<GqlCountries> countriesQuery(@Header("Authorization") String bearerToken,
                                      @Body GqlRequest gqlRequest);

    @POST("/graphql")
    Call<GqlFeed> feedQuery(@Header("Authorization") String bearerToken,
                            @Body GqlRequest gqlRequest);

    @POST("/graphql")
    Call<GqlUser> userMutation(@Header("Authorization") String bearerToken,
                               @Body GqlRequest gqlRequest);

    @POST("/graphql")
    Call<GqlPhoto> photoMutation(@Header("Authorization") String bearerToken,
                                 @Body GqlRequest gqlRequest);

    @POST("/graphql")
    Call<GqlDeletePhoto> deletePhotoMutation(@Header("Authorization") String bearerToken,
                                             @Body GqlRequest gqlRequest);

    @POST("/graphql")
    Call<GqlFriendship> updateFriendshipMutation(@Header("Authorization") String bearerToken,
                                           @Body GqlRequest gqlRequest);
}
