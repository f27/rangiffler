package guru.qa.rangiffler.model.gql.type;

import lombok.Getter;

import java.util.List;

@Getter
public class GqlFeedType extends BaseType {

    private String username;
    private boolean withFriends;
    private GqlSliceType<GqlPhotoType> photos;
    private List<GqlStatType> stat;
}
