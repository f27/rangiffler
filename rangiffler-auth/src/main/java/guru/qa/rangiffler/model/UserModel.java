package guru.qa.rangiffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserModel(
        @JsonProperty("username")
        String username,
        @JsonProperty("country_code")
        String countryCode
) {
}
