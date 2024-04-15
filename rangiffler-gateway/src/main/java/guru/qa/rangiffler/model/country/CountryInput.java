package guru.qa.rangiffler.model.country;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

public record CountryInput(
        @Size(max = 50, message = "Country code length has to be not longer that 50 symbols")
        @JsonProperty("code")
        String code
) {
}
