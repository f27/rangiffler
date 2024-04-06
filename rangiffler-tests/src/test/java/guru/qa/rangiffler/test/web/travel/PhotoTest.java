package guru.qa.rangiffler.test.web.travel;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.Photo;
import guru.qa.rangiffler.jupiter.annotation.User;
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
                .checkAmountOfPhotos(1)
                .checkPhotos(photoForUpload);
    }

    @Test
    @ApiLogin(user = @GenerateUser(photos = @Photo))
    @DisplayName("Фотография должна быть видна")
    void photoShouldBeVisibleTest(@User UserModel user) {
        myTravelsPage
                .checkPhotos(user.photos());
    }
}
