package guru.qa.rangiffler.model.photo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record LikeInput(
        @JsonProperty("user")
        UUID user
) {
}
