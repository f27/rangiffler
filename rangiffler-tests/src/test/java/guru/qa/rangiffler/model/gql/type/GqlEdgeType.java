package guru.qa.rangiffler.model.gql.type;

import lombok.Getter;

@Getter
public class GqlEdgeType<T> extends BaseType {

    private T node;
}
