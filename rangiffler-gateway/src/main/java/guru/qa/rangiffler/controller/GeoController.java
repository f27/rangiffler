package guru.qa.rangiffler.controller;

import guru.qa.rangiffler.api.GeoClient;
import guru.qa.rangiffler.model.country.CountryModel;
import guru.qa.rangiffler.model.photo.PhotoModel;
import guru.qa.rangiffler.model.photo.StatModel;
import guru.qa.rangiffler.model.user.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class GeoController {

    private final GeoClient geoClient;

    @Autowired
    public GeoController(GeoClient geoClient) {
        this.geoClient = geoClient;
    }

    @SchemaMapping(typeName = "User", field = "location")
    public CountryModel location(UserModel user) {
        return geoClient.getCountryByCode(user.countryCode());
    }

    @SchemaMapping(typeName = "Photo", field = "country")
    public CountryModel location(PhotoModel photo) {
        return geoClient.getCountryByCode(photo.countryCode());
    }

    @SchemaMapping(typeName = "Stat", field = "country")
    public CountryModel location(StatModel stat) {
        return geoClient.getCountryByCode(stat.countryCode());
    }

    @QueryMapping
    public List<CountryModel> countries() {
        return geoClient.getAllCountries();
    }
}
