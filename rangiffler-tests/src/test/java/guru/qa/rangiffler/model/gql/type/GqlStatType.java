package guru.qa.rangiffler.model.gql.type;

import lombok.Getter;

@Getter
public class GqlStatType extends BaseType {

    private int count;
    private GqlCountryType country;
}
