package guru.qa.rangiffler.model.gql.type;

import lombok.Getter;

import java.util.UUID;

@Getter
public class GqlLikeType extends BaseType {

    private UUID user;
    private String username;
    private String creationDate;

}
