package guru.qa.rangiffler.model.photo;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rangiffler.grpc.PhotoResponse;
import guru.qa.rangiffler.model.country.CountryModel;

import java.sql.Timestamp;
import java.util.UUID;

public record PhotoModel(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("src")
        String src,
        @JsonProperty("countryCode")
        String countryCode,
        @JsonProperty("country")
        CountryModel country,
        @JsonProperty("description")
        String description,
        @JsonProperty("creationDate")
        Timestamp creationDate,
        @JsonProperty("likes")
        LikesModel likes,
        @JsonProperty("canEdit")
        boolean canEdit
) {
    public static PhotoModel fromGrpcMessage(PhotoResponse message) {
        return new PhotoModel(
                UUID.fromString(message.getPhotoId()),
                message.getSrc(),
                message.getCountryCode(),
                null,
                message.getDescription(),
                Timestamp.valueOf(message.getCreationDate()),
                new LikesModel(
                        message.getLikes().getTotal(),
                        message.getLikes().getLikesList().stream()
                                .map(LikeModel::fromGrpcMessage)
                                .toList()
                ),
                message.getCanEdit()
        );
    }
}
