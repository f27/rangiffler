package guru.qa.rangiffler.model.gql.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rangiffler.model.gql.GqlResponse;
import guru.qa.rangiffler.model.gql.type.GqlFeedType;
import lombok.Getter;

@Getter
public class GqlFeed extends GqlResponse<GqlFeed> {

    @JsonProperty("feed")
    private GqlFeedType feed;
}
