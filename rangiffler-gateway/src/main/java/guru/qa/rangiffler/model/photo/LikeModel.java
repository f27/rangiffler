package guru.qa.rangiffler.model.photo;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rangiffler.grpc.Like;

import java.sql.Timestamp;
import java.util.UUID;

public record LikeModel(
        @JsonProperty("user")
        UUID user,
        @JsonProperty("username")
        String username,
        @JsonProperty("creationDate")
        Timestamp creationDate
) {
    public static LikeModel fromGrpcMessage(Like like, String username) {
        return new LikeModel(
                UUID.fromString(like.getUserId()),
                username,
                Timestamp.valueOf(like.getCreationDate())
        );
    }
}
