package guru.qa.rangiffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rangiffler.grpc.FriendStatus;

import java.util.UUID;

public record UserModel(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("lastname")
        String lastname,
        @JsonProperty("avatar")
        String avatar,
        @JsonProperty("country_code")
        String countryCode,
        @JsonProperty("status")
        FriendStatus status
) {
}
