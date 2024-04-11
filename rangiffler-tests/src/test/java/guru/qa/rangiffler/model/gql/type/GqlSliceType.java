package guru.qa.rangiffler.model.gql.type;

import lombok.Getter;

import java.util.List;

@Getter
public class GqlSliceType<T> extends BaseType {

    private List<GqlEdgeType<T>> edges;
    private GqlPageInfoType pageInfo;

}
