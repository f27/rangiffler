package guru.qa.rangiffler.model.gql.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public abstract class BaseType {

    @JsonProperty("__typename")
    String typename;

}
