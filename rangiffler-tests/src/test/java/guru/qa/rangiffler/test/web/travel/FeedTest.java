package guru.qa.rangiffler.test.web.travel;

import guru.qa.rangiffler.jupiter.annotation.*;
import guru.qa.rangiffler.model.CountryEnum;
import guru.qa.rangiffler.model.FriendStatus;
import guru.qa.rangiffler.model.PhotoModel;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.web.BaseWebTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

@Feature("Путешествия")
@Story("Лента фотографий")
@DisplayName("Лента фотографий")
public class FeedTest extends BaseWebTest {

    @Test
    @ApiLogin(user = @GenerateUser(photos = {
            @Photo(image = "img/photo/1.jpg", description = "Кто тут?"),
            @Photo(image = "img/photo/2.jpg", description = "My second photo"),
    }))
    @DisplayName("Лента фотографий должна показывать мои фотографии")
    void photoShouldBeVisibleTest(@User UserModel user) {
        myTravelsPage
                .checkPhotos(user.photos());
    }

    @Test
    @ApiLogin(user = @GenerateUser(photos = {
            @Photo(image = "img/photo/1.jpg"),
            @Photo(image = "img/photo/2.jpg", country = CountryEnum.KAZAKHSTAN),
    }, friends = {
            @Friend(photos = @Photo),
            @Friend(photos = @Photo(
                    description = "For my friend",
                    image = "img/photo/3.jpg")),
            @Friend(photos = @Photo(
                    description = "Waiting friend",
                    image = "img/photo/4.jpg"),
                    avatar = "img/avatar/1.jpg",
                    status = FriendStatus.INCOME_INVITATION),
            @Friend(photos = @Photo(
                    description = "Will accept friend",
                    image = "img/photo/5.jpg"),
                    avatar = "img/avatar/2.jpg", status = FriendStatus.OUTCOME_INVITATION),
            @Friend(photos = @Photo, status = FriendStatus.NONE),
    }))
    @DisplayName("Лента фотографий при выбранном [Only my travels] должна отображать только мои фотографии")
    void whenOnlyMyTravelsOnlyMyPhotosShouldBeVisibleTest(@User UserModel user) {
        myTravelsPage
                .checkPhotos(user.photos());
    }

    @Test
    @ApiLogin(user = @GenerateUser(photos = {
            @Photo(image = "img/photo/1.jpg"),
            @Photo(image = "img/photo/2.jpg", country = CountryEnum.KAZAKHSTAN),
    }, friends = {
            @Friend(photos = @Photo),
            @Friend(photos = @Photo(
                    description = "For my friend",
                    image = "img/photo/3.jpg")),
            @Friend(photos = @Photo(
                    description = "Waiting friend",
                    image = "img/photo/4.jpg"),
                    avatar = "img/avatar/1.jpg",
                    status = FriendStatus.INCOME_INVITATION),
            @Friend(photos = @Photo(
                    description = "Will accept friend",
                    image = "img/photo/5.jpg"),
                    avatar = "img/avatar/2.jpg", status = FriendStatus.OUTCOME_INVITATION),
            @Friend(photos = @Photo, status = FriendStatus.NONE),
    }))
    @DisplayName("Лента фотографий при выбранном [Only my travels] должна отображать только мои фотографии и моих друзей")
    void whenWithFriendsOnlyMyPhotosAndFriendsPhotosShouldBeVisibleTest(@User UserModel user) {
        myTravelsPage
                .clickWithFriends()
                .checkPhotos(user.photosWithAcceptedFriends());
    }

    @Test
    @ApiLogin(user = @GenerateUser(photos = {
            @Photo(image = "img/photo/1.jpg", country = CountryEnum.RUSSIAN_FEDERATION),
            @Photo(image = "img/photo/2.jpg", country = CountryEnum.KAZAKHSTAN),
            @Photo(image = "img/photo/3.jpg", country = CountryEnum.KAZAKHSTAN),
            @Photo(image = "img/photo/4.jpg", country = CountryEnum.SPAIN),
    }))
    @DisplayName("Лента фотографий при выбранном [Only my travels] после выбора страны на карте отображает только фотографии из этой страны")
    void whenChooseCountryOnWorldMapOnlyPhotosFromThatCountryIsVisibleTest(@User UserModel user) {
        List<PhotoModel> photosFromKazakhstan = user.photos().stream()
                .filter(photoModel -> photoModel.country().equals(CountryEnum.KAZAKHSTAN))
                .toList();

        myTravelsPage
                .getWorldMap().clickCountry(CountryEnum.KAZAKHSTAN);
        myTravelsPage
                .checkPhotos(photosFromKazakhstan);
    }

    @Test
    @ApiLogin(user = @GenerateUser(photos = {
            @Photo(image = "img/photo/1.jpg", country = CountryEnum.RUSSIAN_FEDERATION),
            @Photo(image = "img/photo/2.jpg", country = CountryEnum.KAZAKHSTAN),
            @Photo(image = "img/photo/3.jpg", country = CountryEnum.KAZAKHSTAN),
            @Photo(image = "img/photo/4.jpg", country = CountryEnum.SPAIN),
    }, friends = {
            @Friend(photos = @Photo(country = CountryEnum.KAZAKHSTAN)),
            @Friend(photos = @Photo(country = CountryEnum.KAZAKHSTAN)),
            @Friend(photos = @Photo(country = CountryEnum.UNITED_STATES)),
            @Friend(photos = @Photo(country = CountryEnum.GERMANY)),
    }))
    @DisplayName("Лента фотографий при выбранном [With friends] после выбора страны на карте отображает только фотографии из этой страны")
    void whenChooseCountryOnWorldMapOnlyPhotosWithFriendsFromThatCountryIsVisibleTest(@User UserModel user) {
        List<PhotoModel> photosFromKazakhstan = user.photosWithAcceptedFriends().stream()
                .filter(photoModel -> photoModel.country().equals(CountryEnum.KAZAKHSTAN))
                .toList();

        myTravelsPage.getWorldMap()
                .clickCountry(CountryEnum.KAZAKHSTAN);
        myTravelsPage
                .clickWithFriends()
                .checkPhotos(photosFromKazakhstan);
    }
}
