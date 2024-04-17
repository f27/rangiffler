package guru.qa.rangiffler.api.rest;

import guru.qa.rangiffler.model.gql.GqlRequest;
import guru.qa.rangiffler.model.gql.response.GqlCountries;
import guru.qa.rangiffler.model.gql.response.GqlFeed;
import guru.qa.rangiffler.model.gql.response.GqlUser;
import guru.qa.rangiffler.model.gql.response.GqlUsers;
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
}
