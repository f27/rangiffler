package guru.qa.rangiffler.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rangiffler.model.country.CountryInput;
import jakarta.validation.constraints.Size;

public record UpdateUserInput(
        @Size(max = 50, message = "First name length has to be not longer that 50 symbols")
        @JsonProperty("firstname")
        String firstname,
        @Size(max = 50, message = "Surname length has to be not longer that 50 symbols")
        @JsonProperty("surname")
        String surname,
        @JsonProperty("avatar")
        String avatar,
        @JsonProperty("location")
        CountryInput location
) {
}
