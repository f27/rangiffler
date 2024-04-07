package guru.qa.rangiffler.test.web.travel;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.model.CountryEnum;
import guru.qa.rangiffler.model.PhotoModel;
import guru.qa.rangiffler.test.web.BaseWebTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Feature("Путешествия")
@Story("Добавление фотографии")
@DisplayName("Добавление фотографии")
public class AddPhotoTest extends BaseWebTest {

    @Test
    @ApiLogin
    @DisplayName("Добавление фотографии только с картинкой")
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
    @ApiLogin
    @DisplayName("Добавление фотографии с картинкой и описанием")
    void photoWithDescriptionUploadTest() {
        PhotoModel photoForUpload = PhotoModel.create("img/photo/2.jpg", "Be better");
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
    @ApiLogin
    @DisplayName("Добавление фотографии с картинкой, описанием и страной")
    void photoWithDescriptionAndCountryUploadTest() {
        PhotoModel photoForUpload = PhotoModel.create("img/photo/2.jpg", "Be better", CountryEnum.KAZAKHSTAN);
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
    @ApiLogin
    @DisplayName("Добавление фотографии с картинкой и страной")
    void photoWithCountryUploadTest() {
        PhotoModel photoForUpload = PhotoModel.create("img/photo/3.jpg", CountryEnum.UKRAINE);
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
}
