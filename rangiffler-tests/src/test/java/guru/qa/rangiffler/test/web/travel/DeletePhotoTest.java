package guru.qa.rangiffler.test.web.travel;

import com.codeborne.selenide.Selenide;
import guru.qa.rangiffler.jupiter.annotation.*;
import guru.qa.rangiffler.model.CountryEnum;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.web.BaseWebTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Feature("Путешествия")
@Story("Удаление фотографии")
@DisplayName("Удаление фотографии")
public class DeletePhotoTest extends BaseWebTest {

    @Test
    @ApiLogin(user = @GenerateUser(photos = {
            @Photo(country = CountryEnum.EGYPT, description = "First photo from Egypt"),
            @Photo(country = CountryEnum.ITALY, description = "Second photo from Italy"),
    }))
    @DisplayName("Удаление фотографии возможно если она моя")
    void canDeleteMyPhotoTest(@User UserModel user) {
        myTravelsPage
                .checkPhotos(user.photos())
                .deletePhoto(user.photos().get(0))
                .getSnackbar().messageShouldHaveText("Post deleted");
        user.photos().remove(0);
        myTravelsPage
                .checkPhotos(user.photos());
        Selenide.refresh();
        myTravelsPage
                .checkPhotos(user.photos());
    }

    @Test
    @ApiLogin(user = @GenerateUser(friends = {
            @Friend(photos = @Photo(description = "First friend photo")),
    }))
    @DisplayName("Под фотографией друга не должно быть кнопки удалить")
    void canNotDeleteFriendsPhotoTest(@User UserModel user) {
        myTravelsPage
                .clickWithFriends()
                .checkPhotos(user.photosWithAcceptedFriends())
                .checkPhotoHasNoDeleteButton(user.photosWithAcceptedFriends().get(0));
    }
}
