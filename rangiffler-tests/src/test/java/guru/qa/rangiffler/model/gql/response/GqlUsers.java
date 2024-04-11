package guru.qa.rangiffler.model.gql.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rangiffler.model.gql.GqlResponse;
import guru.qa.rangiffler.model.gql.type.GqlSliceType;
import guru.qa.rangiffler.model.gql.type.GqlUserType;
import lombok.Getter;

@Getter
public class GqlUsers extends GqlResponse<GqlUsers> {

    @JsonProperty("users")
    private GqlSliceType<GqlUserType> users;

}
