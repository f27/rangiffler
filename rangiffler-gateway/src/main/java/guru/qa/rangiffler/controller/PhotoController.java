package guru.qa.rangiffler.controller;

import guru.qa.rangiffler.api.PhotoClient;
import guru.qa.rangiffler.model.photo.FeedModel;
import guru.qa.rangiffler.model.photo.PhotoInput;
import guru.qa.rangiffler.model.photo.PhotoModel;
import guru.qa.rangiffler.model.photo.StatModel;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class PhotoController {

    private final PhotoClient photoClient;

    @Autowired
    public PhotoController(PhotoClient photoClient) {
        this.photoClient = photoClient;
    }

    @SchemaMapping(typeName = "Feed", field = "photos")
    public Slice<PhotoModel> photos(FeedModel feed, @Argument int page, @Argument int size) {
        return photoClient.getPhotos(feed.username(), feed.withFriends(), page, size);
    }

    @SchemaMapping(typeName = "Feed", field = "stat")
    public List<StatModel> stat(FeedModel feed) {
        return photoClient.getStat(feed.username(), feed.withFriends());
    }

    @QueryMapping
    public FeedModel feed(@AuthenticationPrincipal Jwt principal,
                          @Argument @Nonnull Boolean withFriends) {
        String username = principal.getClaim("sub");
        return new FeedModel(
                username,
                withFriends,
                null,
                null
        );
    }

    @MutationMapping
    public PhotoModel photo(@AuthenticationPrincipal Jwt principal,
                            @Argument @Valid PhotoInput input) {
        String username = principal.getClaim("sub");
        return photoClient.mutatePhoto(username, input);
    }

    @MutationMapping
    public Boolean deletePhoto(@AuthenticationPrincipal Jwt principal,
                               @Argument UUID id) {
        String username = principal.getClaim("sub");
        return photoClient.deletePhoto(username, id);
    }
}
