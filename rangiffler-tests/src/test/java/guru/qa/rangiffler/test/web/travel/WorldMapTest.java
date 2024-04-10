package guru.qa.rangiffler.test.web.travel;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.Friend;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.Photo;
import guru.qa.rangiffler.model.CountryEnum;
import guru.qa.rangiffler.test.web.BaseWebTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Feature("Путешествия")
@Story("Карта мира")
@DisplayName("Карта мира")
public class WorldMapTest extends BaseWebTest {

    @Test
    @ApiLogin
    @DisplayName("Карта мира должна менять заголовок при клике по стране")
    void worldMapShouldHaveTitleWithChosenCountryTest() {
        myTravelsPage.getWorldMap()
                .clickCountry(CountryEnum.KAZAKHSTAN)
                .checkSelectedCountry(CountryEnum.KAZAKHSTAN)
                .clickCountry(CountryEnum.HONDURAS)
                .checkSelectedCountry(CountryEnum.HONDURAS);
    }

    @Test
    @ApiLogin
    @DisplayName("Карта мира должна показывать, что выбраны все страны до выбора страны")
    void worldMapShouldHaveTitleWithAllCountriesByDefaultTest() {
        myTravelsPage.getWorldMap()
                .checkSelectedAllCountries();
    }

    @Test
    @ApiLogin
    @DisplayName("Карта мира должна показывать, что выбраны все страны до выбора страны")
    void worldMapShouldHaveTitleWithAllCountriesAfterAllCountriesButtonClickedTest() {
        myTravelsPage.getWorldMap()
                .clickCountry(CountryEnum.CHINA)
                .checkSelectedCountry(CountryEnum.CHINA);
        myTravelsPage
                .clickAllCountries();
        myTravelsPage.getWorldMap()
                .checkSelectedAllCountries();
    }

    @Test
    @ApiLogin(user = @GenerateUser(photos = @Photo(country = CountryEnum.EGYPT)))
    @DisplayName("Карта мира при [Only my travels] должна подсвечиваться страну из которой есть мои фотографии")
    void worldMapShouldHighlightCountryWithPhotosTest() {
        myTravelsPage.getWorldMap()
                .checkCountryIsNotHighlighted(CountryEnum.RUSSIAN_FEDERATION)
                .checkCountryIsHighlighted(CountryEnum.EGYPT);
    }

    @Test
    @ApiLogin(user = @GenerateUser(
            friends = @Friend(photos = @Photo(country = CountryEnum.EGYPT))
    ))
    @DisplayName("Карта мира при [Only my travels] не должна подсвечивать страну из которой есть фотографии друга")
    void worldMapShouldNotHighlightCountryWithFriendsPhotoTest() {
        myTravelsPage.getWorldMap()
                .checkCountryIsNotHighlighted(CountryEnum.EGYPT);
    }

    @Test
    @ApiLogin(user = @GenerateUser(photos = @Photo(country = CountryEnum.EGYPT)))
    @DisplayName("Карта мира при [With friends] должна подсвечивать страну из которой есть мои фотографии")
    void worldMapWithFriendsShouldHighlightCountryWithPhotosTest() {
        myTravelsPage
                .clickWithFriends()
                .getWorldMap()
                .checkCountryIsNotHighlighted(CountryEnum.RUSSIAN_FEDERATION)
                .checkCountryIsHighlighted(CountryEnum.EGYPT);
    }

    @Test
    @ApiLogin(user = @GenerateUser(
            friends = @Friend(photos = @Photo(country = CountryEnum.EGYPT))
    ))
    @DisplayName("Карта мира при [With friends] должна подсвечивать страну из которой есть фотографии друга")
    void worldMapWithFriendsShouldNotHighlightCountryWithFriendsPhotoTest() {
        myTravelsPage
                .clickWithFriends()
                .getWorldMap()
                .checkCountryIsHighlighted(CountryEnum.EGYPT);
    }
}
