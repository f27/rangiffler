package guru.qa.rangiffler.model.country;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CountryInput(
        @JsonProperty("code")
        String code
) {
}
