package guru.qa.rangiffler.test.web.travel;

import com.codeborne.selenide.Selenide;
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
    @DisplayName("Когда выбран [With friends] должны быть видны мои фотографии и фотографии друзей")
    void whenWithFriendsOnlyMyPhotosAndFriendsPhotosShouldBeVisibleTest(@User UserModel user) {
        myTravelsPage
                .clickWithFriends()
                .checkPhotos(user.photosWithAcceptedFriends());
    }

    @Test
    @ApiLogin(user = @GenerateUser(photos = @Photo(description = "Happy life")))
    @DisplayName("На свою фотографию можно поставить и убрать лайк")
    void addAndRemoveLikeForMyPhotoTest(@User UserModel user) {
        final String description = user.photos().get(0).description();
        myTravelsPage
                .checkPhotoWithDescriptionHasAmountOfLikes(description, 0)
                .clickLikeToPhotoWithDescription(description)
                .getSnackbar().messageShouldHaveText("Like status was changed");
        myTravelsPage
                .checkPhotoWithDescriptionHasAmountOfLikes(description, 1);
        Selenide.refresh();
        myTravelsPage
                .checkPhotoWithDescriptionHasAmountOfLikes(description, 1)
                .clickLikeToPhotoWithDescription(description)
                .getSnackbar().messageShouldHaveText("Like status was changed");
        myTravelsPage
                .checkPhotoWithDescriptionHasAmountOfLikes(description, 0);
        Selenide.refresh();
        myTravelsPage
                .checkPhotoWithDescriptionHasAmountOfLikes(description, 0);
    }

    @Test
    @ApiLogin(user = @GenerateUser(
            friends = @Friend(photos = @Photo(description = "Friends photo"))
    ))
    @DisplayName("На фотографию друга можно поставить и убрать лайк")
    void addAndRemoveLikeForFriendsPhotoTest(@User UserModel user) {
        final String description = user.friends().get(0).photos().get(0).description();
        myTravelsPage
                .clickWithFriends()
                .checkPhotoWithDescriptionHasAmountOfLikes(description, 0)
                .clickLikeToPhotoWithDescription(description)
                .getSnackbar().messageShouldHaveText("Like status was changed");
        myTravelsPage
                .checkPhotoWithDescriptionHasAmountOfLikes(description, 1);
        Selenide.refresh();
        myTravelsPage
                .clickWithFriends()
                .checkPhotoWithDescriptionHasAmountOfLikes(description, 1)
                .clickLikeToPhotoWithDescription(description)
                .getSnackbar().messageShouldHaveText("Like status was changed");
        myTravelsPage
                .checkPhotoWithDescriptionHasAmountOfLikes(description, 0);
        Selenide.refresh();
        myTravelsPage
                .clickWithFriends()
                .checkPhotoWithDescriptionHasAmountOfLikes(description, 0);
    }
}
