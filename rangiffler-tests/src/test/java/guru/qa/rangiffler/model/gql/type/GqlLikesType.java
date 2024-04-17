package guru.qa.rangiffler.model.gql.type;

import lombok.Getter;

import java.util.List;

@Getter
public class GqlLikesType extends BaseType {

    private int total;
    private List<GqlLikeType> likes;

}
