package guru.qa.rangiffler.test.web.travel;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.model.CountryEnum;
import guru.qa.rangiffler.model.PhotoModel;
import guru.qa.rangiffler.test.web.BaseWebTest;
import guru.qa.rangiffler.util.DataUtil;
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
    @DisplayName("Добавление фотографии c длинным описанием (255 символов)")
    void photoWithLongDescriptionUploadTest() {
        PhotoModel photoForUpload = PhotoModel.create("img/photo/1.jpg", DataUtil.generateStringWithLength(255));
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
    @DisplayName("Добавление фотографии c очень длинным описанием (256 символов) показывает ошибку")
    void photoWithTooLongDescriptionUploadTest() {
        PhotoModel photoForUpload = PhotoModel.create("img/photo/1.jpg", DataUtil.generateStringWithLength(256));
        myTravelsPage
                .clickAddPhoto()
                .getAddPhotoModal()
                .setImage(photoForUpload.photo())
                .setDescription(photoForUpload.description())
                .clickSave()
                .checkDescriptionHasError("Description length has to be not longer that 255 symbols");
    }

    @Test
    @ApiLogin
    @DisplayName("Добавление фотографии без загрузки картинки вызывает ошибку")
    void photoWithoutImageUploadTest() {
        String description = DataUtil.generateStringWithLength(50);
        myTravelsPage
                .clickAddPhoto()
                .getAddPhotoModal()
                .setDescription(description)
                .clickSave()
                .checkImageHasError("Please upload an image");
    }

    @Test
    @ApiLogin
    @DisplayName("Добавление фотографии без загрузки картинки и слишком длинным описанием (256 символов) вызывает ошибку")
    void photoWithoutImageAndDescriptionUploadTest() {
        String description = DataUtil.generateStringWithLength(256);
        myTravelsPage
                .clickAddPhoto()
                .getAddPhotoModal()
                .setDescription(description)
                .clickSave()
                .checkImageHasError("Please upload an image")
                .checkDescriptionHasError("Description length has to be not longer that 255 symbols");
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
