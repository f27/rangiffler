package guru.qa.rangiffler.model.photo;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rangiffler.model.country.CountryModel;

public record StatModel(
        @JsonProperty("count")
        int count,
        @JsonProperty("countryCode")
        String countryCode,
        @JsonProperty("country")
        CountryModel country
) {
}
