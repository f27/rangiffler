package guru.qa.rangiffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rangiffler.grpc.AllCountriesResponse;
import guru.qa.grpc.rangiffler.grpc.CountryResponse;

import java.util.List;
import java.util.UUID;

public record CountryModel(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("code")
        String code,
        @JsonProperty("name")
        String name,
        @JsonProperty("flag")
        String flag
) {
    public static CountryModel fromGrpcMessage(CountryResponse country) {
        return new CountryModel(
                UUID.fromString(country.getId()),
                country.getCode(),
                country.getName(),
                country.getFlag()
        );
    }

    public static List<CountryModel> fromGrpcMessage(AllCountriesResponse countries) {
        return countries.getAllCountriesList().stream()
                .map(CountryModel::fromGrpcMessage)
                .toList();
    }
}
