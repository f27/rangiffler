package guru.qa.rangiffler.model.gql.type;

import lombok.Getter;

import java.util.UUID;

@Getter
public class GqlPhotoType extends BaseType {

    private boolean canEdit;
    private UUID id;
    private String src;
    private GqlCountryType country;
    private String description;
    private String creationDate;
    private GqlLikesType likes;

}
