package guru.qa.rangiffler.model.photo;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rangiffler.model.country.CountryInput;

import java.util.UUID;

public record PhotoInput(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("src")
        String src,
        @JsonProperty("country")
        CountryInput country,
        @JsonProperty("description")
        String description,
        @JsonProperty("like")
        LikeInput like
) {
}
