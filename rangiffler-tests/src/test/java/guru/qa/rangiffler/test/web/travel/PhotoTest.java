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

@Feature("Путешествия")
@Story("Фотография")
@DisplayName("Фотография")
public class PhotoTest extends BaseWebTest {

    @Test
    @ApiLogin
    @DisplayName("Фотография может быть загружена")
    void photoUploadTest() {
        PhotoModel photoForUpload = PhotoModel.create("img/photo/1.jpg");
        myTravelsPage
                .clickAddPhoto()
                .getAddPhotoModal()
                .uploadPhoto(photoForUpload)
                .clickSave();
        myTravelsPage
                .getSnackbar().messageShouldHaveText("New post created");
        myTravelsPage
                .checkPhotos(photoForUpload);
    }

    @Test
    @ApiLogin(user = @GenerateUser(photos = @Photo))
    @DisplayName("Фотография должна быть видна")
    void photoShouldBeVisibleTest(@User UserModel user) {
        myTravelsPage
                .checkPhotos(user.photos());
    }

    @Test
    @ApiLogin(user = @GenerateUser(photos = {
            @Photo(image = "img/photo/1.jpg"),
            @Photo(image = "img/photo/2.jpg", country = CountryEnum.KAZAKHSTAN),
            @Photo(image = "img/photo/3.jpg", description = "Good party!"),
            @Photo(image = "img/photo/4.jpg", country = CountryEnum.KENYA, description = "Nice summer"),
    }))
    @DisplayName("Все фотографии должны быть видны")
    void allPhotosShouldBeVisibleTest(@User UserModel user) {
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
    @DisplayName("Когда выбран [Only my travels] должны быть видны только мои фотографии")
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
    @DisplayName("Когда выбран [Only my travels] должны быть видны только мои фотографии")
    void whenWithFriendsOnlyMyPhotosAndFriendsPhotosShouldBeVisibleTest(@User UserModel user) {
        myTravelsPage
                .clickWithFriends()
                .checkPhotos(user.photosWithAcceptedFriends());
    }
}
