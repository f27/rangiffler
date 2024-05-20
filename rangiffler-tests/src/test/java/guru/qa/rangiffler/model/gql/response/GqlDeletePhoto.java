package guru.qa.rangiffler.model.gql.response;

import guru.qa.rangiffler.model.gql.GqlResponse;
import lombok.Getter;

@Getter
public class GqlDeletePhoto extends GqlResponse<GqlDeletePhoto> {

    private boolean deletePhoto;

}
