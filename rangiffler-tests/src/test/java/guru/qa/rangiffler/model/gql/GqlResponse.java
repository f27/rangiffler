package guru.qa.rangiffler.model.gql;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class GqlResponse<T extends GqlResponse<T>> {

    private T data;
    private List<GqlError> errors;

}
