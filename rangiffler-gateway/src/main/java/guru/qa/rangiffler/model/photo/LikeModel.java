package guru.qa.rangiffler.model.photo;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rangiffler.grpc.Like;

import java.util.UUID;

public record LikeModel(
        @JsonProperty("user")
        UUID user
) {
    public static LikeModel fromGrpcMessage(Like like) {
        return new LikeModel(UUID.fromString(like.getUserId()));
    }
}
