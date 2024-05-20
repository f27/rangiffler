package guru.qa.rangiffler.model.gql.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rangiffler.model.gql.GqlResponse;
import guru.qa.rangiffler.model.gql.type.GqlUserType;
import lombok.Getter;

@Getter
public class GqlFriendship extends GqlResponse<GqlFriendship> {

    @JsonProperty("friendship")
    private GqlUserType friendship;
}
