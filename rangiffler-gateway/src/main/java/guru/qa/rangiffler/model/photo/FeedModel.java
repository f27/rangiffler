package guru.qa.rangiffler.model.photo;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.UUID;

public record FeedModel(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("withFriends")
        Boolean withFriends,
        @JsonProperty("photos")
        Slice<PhotoModel> photos,
        @JsonProperty("stat")
        List<StatModel> stat,
        @JsonProperty("friendsIds")
        List<UUID> friendsIds
) {
}
