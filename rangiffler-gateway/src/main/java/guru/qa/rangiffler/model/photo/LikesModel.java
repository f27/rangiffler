package guru.qa.rangiffler.model.photo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record LikesModel(
        @JsonProperty("total")
        int total,
        @JsonProperty("likes")
        List<LikeModel> likes
) {
}
