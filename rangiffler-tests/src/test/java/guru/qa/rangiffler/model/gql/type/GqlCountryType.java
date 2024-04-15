package guru.qa.rangiffler.model.gql.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.UUID;

@Getter
public class GqlCountryType extends BaseType {

    @JsonProperty("id")
    private UUID id;
    @JsonProperty("code")
    private String code;
    @JsonProperty("name")
    private String name;
    @JsonProperty("flag")
    private String flag;

}