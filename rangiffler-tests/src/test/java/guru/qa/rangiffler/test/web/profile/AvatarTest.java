package guru.qa.rangiffler.test.web.profile;

import com.codeborne.selenide.Selenide;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.web.BaseWebTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Feature("Профиль аккаунта")
@Story("Аватар")
@DisplayName("Аватар")
public class AvatarTest extends BaseWebTest {

    @BeforeEach
    void openProfile() {
        myTravelsPage
                .getDrawer().clickProfile();
    }

    @Test
    @ApiLogin
    @DisplayName("Аватар у пользователя без аватара должен отображать иконку по умолчанию")
    void avatarShouldHaveDefaultIconTest() {
        profilePage
                .checkAvatarHasDefaultIcon();
    }

    @Test
    @ApiLogin(user = @GenerateUser(avatar = "img/avatar/1.jpg"))
    @DisplayName("Аватар у пользователя с автаром должен быть виден")
    void avatarShouldBeVisibleTest(@User UserModel user) {
        profilePage
                .checkAvatar(user.getAvatar());
    }

    @Test
    @ApiLogin
    @DisplayName("Аватар может быть загружен")
    void avatarCanBeUploadedTest() {
        String avatarClasspath = "img/avatar/2.jpg";
        profilePage
                .uploadAvatar(avatarClasspath)
                .checkAvatar(avatarClasspath)
                .clickSave()
                .getSnackbar().messageShouldHaveText("Your profile is successfully updated");
        Selenide.refresh();
        profilePage
                .checkAvatar(avatarClasspath);
    }

    @Test
    @ApiLogin(user = @GenerateUser(avatar = "img/avatar/1.jpg"))
    @DisplayName("Аватар может быть обновлен")
    void avatarCanBeUpdatedTest(@User UserModel user) {
        String updatedAvatarClasspath = "img/avatar/2.jpg";
        profilePage
                .checkAvatar(user.getAvatar())
                .uploadAvatar(updatedAvatarClasspath)
                .checkAvatar(updatedAvatarClasspath)
                .clickSave()
                .getSnackbar().messageShouldHaveText("Your profile is successfully updated");
        Selenide.refresh();
        profilePage
                .checkAvatar(updatedAvatarClasspath);
    }

    @Test
    @ApiLogin(user = @GenerateUser(avatar = "img/avatar/1.jpg"))
    @DisplayName("Аватар может быть обновлен после нажатия кнопки [Reset]")
    void avatarCanBeUpdatedAfterResetButtonClickTest(@User UserModel user) {
        String updatedAvatarClasspath = "img/avatar/2.jpg";
        profilePage
                .checkAvatar(user.getAvatar())
                .uploadAvatar(updatedAvatarClasspath)
                .checkAvatar(updatedAvatarClasspath)
                .clickReset()
                .checkAvatar(user.getAvatar())
                .uploadAvatar(updatedAvatarClasspath)
                .checkAvatar(updatedAvatarClasspath)
                .clickSave()
                .getSnackbar().messageShouldHaveText("Your profile is successfully updated");
        Selenide.refresh();
        profilePage
                .checkAvatar(updatedAvatarClasspath);
    }

    @Test
    @ApiLogin
    @DisplayName("Аватар не должен быть сохранен если не нажать [Save]")
    void avatarShouldNotBeSavedIfSaveButtonNotClickedTest() {
        String updatedAvatarClasspath = "img/avatar/1.jpg";
        profilePage
                .checkAvatarHasDefaultIcon()
                .uploadAvatar(updatedAvatarClasspath)
                .checkAvatar(updatedAvatarClasspath);
        Selenide.refresh();
        profilePage
                .checkAvatarHasDefaultIcon();
    }
}
