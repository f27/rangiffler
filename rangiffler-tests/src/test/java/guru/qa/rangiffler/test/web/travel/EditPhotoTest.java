package guru.qa.rangiffler.test.web.travel;

import com.codeborne.selenide.Selenide;
import guru.qa.rangiffler.jupiter.annotation.*;
import guru.qa.rangiffler.model.CountryEnum;
import guru.qa.rangiffler.model.PhotoModel;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.web.BaseWebTest;
import guru.qa.rangiffler.util.DataUtil;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Feature("Путешествия")
@Story("Редактирование фотографии")
@DisplayName("Редактирование фотографии")
public class EditPhotoTest extends BaseWebTest {

    @Test
    @ApiLogin(user = @GenerateUser(photos = {
            @Photo(country = CountryEnum.EGYPT, description = "First photo from Egypt"),
    }))
    @DisplayName("Редактирование фотографии возможно если она моя: сменить страну")
    void editMyPhotoCountryTest(@User UserModel user) {
        PhotoModel updatedPhoto = new PhotoModel(
                null,
                CountryEnum.UNITED_KINGDOM,
                user.photos().get(0).description(),
                user.photos().get(0).photo()
        );
        myTravelsPage
                .checkPhotos(user.photos())
                .clickEditPhoto(user.photos().get(0))
                .getEditPhotoModal()
                .selectCountry(updatedPhoto.country())
                .clickSave();
        myTravelsPage
                .getSnackbar().messageShouldHaveText("Post updated");
        user.photos().remove(0);
        user.photos().add(updatedPhoto);
        myTravelsPage
                .checkPhotos(user.photos());
        Selenide.refresh();
        myTravelsPage
                .checkPhotos(user.photos());
    }

    @Test
    @ApiLogin(user = @GenerateUser(photos = {
            @Photo(country = CountryEnum.EGYPT, description = "First photo from Egypt"),
    }))
    @DisplayName("Редактирование фотографии возможно если она моя: сменить описание")
    void editMyPhotoDescriptionTest(@User UserModel user) {
        PhotoModel updatedPhoto = new PhotoModel(
                null,
                user.photos().get(0).country(),
                "I updated description!",
                user.photos().get(0).photo()
        );
        myTravelsPage
                .checkPhotos(user.photos())
                .clickEditPhoto(user.photos().get(0))
                .getEditPhotoModal()
                .setDescription(updatedPhoto.description())
                .clickSave();
        myTravelsPage
                .getSnackbar().messageShouldHaveText("Post updated");
        user.photos().remove(0);
        user.photos().add(updatedPhoto);
        myTravelsPage
                .checkPhotos(user.photos());
        Selenide.refresh();
        myTravelsPage
                .checkPhotos(user.photos());
    }

    @Test
    @ApiLogin(user = @GenerateUser(photos = {
            @Photo(country = CountryEnum.EGYPT, description = "First photo from Egypt"),
    }))
    @DisplayName("Редактирование фотографии возможно если она моя: сменить страну и описание")
    void editMyPhotoCountryAndDescriptionTest(@User UserModel user) {
        PhotoModel updatedPhoto = new PhotoModel(
                null,
                CountryEnum.getRandom(),
                "I updated description!",
                user.photos().get(0).photo()
        );
        myTravelsPage
                .checkPhotos(user.photos())
                .clickEditPhoto(user.photos().get(0))
                .getEditPhotoModal()
                .selectCountry(updatedPhoto.country())
                .setDescription(updatedPhoto.description())
                .clickSave();
        myTravelsPage
                .getSnackbar().messageShouldHaveText("Post updated");
        user.photos().remove(0);
        user.photos().add(updatedPhoto);
        myTravelsPage
                .checkPhotos(user.photos());
        Selenide.refresh();
        myTravelsPage
                .checkPhotos(user.photos());
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = {
            @Friend(photos = @Photo)
    }))
    @DisplayName("Редактирование фотографии недоступно если она друга")
    void ifPhotoIsNotMineEditIsNotAvailableTest(@User UserModel user) {
        myTravelsPage
                .clickWithFriends()
                .checkPhotos(user.photosWithAcceptedFriends())
                .checkPhotoHasNoEditButton(user.photosWithAcceptedFriends().get(0));
    }

    @Test
    @ApiLogin(user = @GenerateUser(photos = @Photo))
    @DisplayName("Редактирование фотографии на длинное описание (255 символов)")
    void photoWithLongDescriptionEditTest(@User UserModel user) {
        PhotoModel originalPhoto = user.photos().get(0);
        PhotoModel updatedPhoto = new PhotoModel(
                originalPhoto.id(),
                originalPhoto.country(),
                DataUtil.generateStringWithLength(255),
                originalPhoto.photo()
        );
        myTravelsPage
                .clickEditPhoto(originalPhoto)
                .getEditPhotoModal()
                .setDescription(updatedPhoto.description())
                .clickSave();
        myTravelsPage
                .getSnackbar().messageShouldHaveText("Post updated");
        myTravelsPage
                .checkPhotos(updatedPhoto);
    }

    @Test
    @ApiLogin(user = @GenerateUser(photos = @Photo))
    @DisplayName("Редактирование фотографии на очень длинное описание (256 символов)")
    void photoWithTooLongDescriptionEditTest(@User UserModel user) {
        String description = DataUtil.generateStringWithLength(256);
        myTravelsPage
                .clickEditPhoto(user.photos().get(0))
                .getEditPhotoModal()
                .setDescription(description)
                .clickSave()
                .checkDescriptionHasError("Description length has to be not longer that 255 symbols");
    }
}
