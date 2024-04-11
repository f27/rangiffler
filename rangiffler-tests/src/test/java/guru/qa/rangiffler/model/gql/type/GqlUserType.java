package guru.qa.rangiffler.model.gql.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rangiffler.grpc.FriendStatus;
import guru.qa.rangiffler.model.gql.response.GqlUser;
import lombok.Getter;

import java.util.UUID;

@Getter
public class GqlUserType extends BaseType {

    @JsonProperty("friends")
    private GqlSliceType<GqlUser> friends;
    @JsonProperty("incomeInvitations")
    private GqlSliceType<GqlUser> incomeInvitations;
    @JsonProperty("outcomeInvitations")
    private GqlSliceType<GqlUser> outcomeInvitations;
    @JsonProperty("location")
    private GqlCountryType location;
    @JsonProperty("id")
    private UUID id;
    @JsonProperty("username")
    private String username;
    @JsonProperty("firstname")
    private String firstname;
    @JsonProperty("surname")
    private String surname;
    @JsonProperty("avatar")
    private String avatar;
    @JsonProperty("friendStatus")
    private FriendStatus friendStatus;

}
