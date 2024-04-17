package guru.qa.rangiffler.model.gql.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rangiffler.model.gql.GqlResponse;
import guru.qa.rangiffler.model.gql.type.GqlCountryType;
import lombok.Getter;

import java.util.List;

@Getter
public class GqlCountries extends GqlResponse<GqlCountries> {

    @JsonProperty("countries")
    private List<GqlCountryType> countries;
}
