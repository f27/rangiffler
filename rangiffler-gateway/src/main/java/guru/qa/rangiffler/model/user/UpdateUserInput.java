package guru.qa.rangiffler.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rangiffler.model.country.CountryInput;

public record UpdateUserInput(
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("surname")
        String surname,
        @JsonProperty("avatar")
        String avatar,
        @JsonProperty("location")
        CountryInput location
) {
}
