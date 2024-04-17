package guru.qa.rangiffler.model.gql.response;

import guru.qa.rangiffler.model.gql.GqlResponse;
import guru.qa.rangiffler.model.gql.type.GqlPhotoType;
import lombok.Getter;

@Getter
public class GqlPhoto extends GqlResponse<GqlPhoto> {

    private GqlPhotoType photo;

}
