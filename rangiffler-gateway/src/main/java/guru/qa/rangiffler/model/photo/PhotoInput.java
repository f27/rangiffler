package guru.qa.rangiffler.model.photo;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rangiffler.model.country.CountryInput;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record PhotoInput(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("src")
        String src,
        @JsonProperty("country")
        CountryInput country,
        @JsonProperty("description")
        @Size(max = 255, message = "Description length has to be not longer that 255 symbols")
        String description,
        @JsonProperty("like")
        LikeInput like
) {
}
