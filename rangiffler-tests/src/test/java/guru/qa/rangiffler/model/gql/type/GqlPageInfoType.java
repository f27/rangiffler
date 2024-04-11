package guru.qa.rangiffler.model.gql.type;

import lombok.Getter;

@Getter
public class GqlPageInfoType extends BaseType {
    private boolean hasPreviousPage;
    private boolean hasNextPage;
}