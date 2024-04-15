package guru.qa.rangiffler.test.web.travel;

import com.codeborne.selenide.Selenide;
import guru.qa.rangiffler.jupiter.annotation.*;
import guru.qa.rangiffler.model.PhotoModel;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.web.BaseWebTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Feature("Путешествия")
@Story("Лайки фотографии")
@DisplayName("Лайки фотографии")
public class PhotoLikeTest extends BaseWebTest {

    @Test
    @ApiLogin(user = @GenerateUser(photos = @Photo(description = "Happy life")))
    @DisplayName("Лайки фотографии можно добавить и убрать под своей фотографией")
    void addAndRemoveLikeForMyPhotoTest(@User UserModel user) {
        final PhotoModel photo = user.photos().get(0);
        myTravelsPage
                .checkPhotoHasAmountOfLikes(photo, 0)
                .clickLikeToPhoto(photo)
                .getSnackbar().messageShouldHaveText("Like status was changed");
        myTravelsPage
                .checkPhotoHasAmountOfLikes(photo, 1);
        Selenide.refresh();
        myTravelsPage
                .checkPhotoHasAmountOfLikes(photo, 1)
                .clickLikeToPhoto(photo)
                .getSnackbar().messageShouldHaveText("Like status was changed");
        myTravelsPage
                .checkPhotoHasAmountOfLikes(photo, 0);
        Selenide.refresh();
        myTravelsPage
                .checkPhotoHasAmountOfLikes(photo, 0);
    }

    @Test
    @ApiLogin(user = @GenerateUser(
            friends = @Friend(photos = @Photo(description = "Friends photo"))
    ))
    @DisplayName("Лайки фотографии можно добавить и убрать под фотографией друга")
    void addAndRemoveLikeForFriendsPhotoTest(@User UserModel user) {
        final PhotoModel photo = user.photosWithAcceptedFriends().get(0);
        myTravelsPage
                .clickWithFriends()
                .checkPhotoHasAmountOfLikes(photo, 0)
                .clickLikeToPhoto(photo)
                .getSnackbar().messageShouldHaveText("Like status was changed");
        myTravelsPage
                .checkPhotoHasAmountOfLikes(photo, 1);
        Selenide.refresh();
        myTravelsPage
                .clickWithFriends()
                .checkPhotoHasAmountOfLikes(photo, 1)
                .clickLikeToPhoto(photo)
                .getSnackbar().messageShouldHaveText("Like status was changed");
        myTravelsPage
                .checkPhotoHasAmountOfLikes(photo, 0);
        Selenide.refresh();
        myTravelsPage
                .clickWithFriends()
                .checkPhotoHasAmountOfLikes(photo, 0);
    }
}
