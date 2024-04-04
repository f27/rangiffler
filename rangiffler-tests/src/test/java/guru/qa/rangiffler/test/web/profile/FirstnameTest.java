package guru.qa.rangiffler.test.web.profile;

import com.codeborne.selenide.Selenide;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.web.BaseWebTest;
import guru.qa.rangiffler.util.DataUtil;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Feature("Профиль аккаунта")
@Story("Имя пользователя")
@DisplayName("Имя пользователя")
public class FirstnameTest extends BaseWebTest {

    @BeforeEach
    void openProfile() {
        myTravelsPage
                .goToProfile();
    }

    @Test
    @ApiLogin
    @DisplayName("Имя пользователя можно заполнить")
    void firstSetUsername() {
        String updatedFirstname = DataUtil.generateRandomFirstname();
        profilePage
                .checkUsername("")
                .setUsername(updatedFirstname)
                .clickSave()
                .checkSnackbarMessage("Your profile is successfully updated");
        Selenide.refresh();
        profilePage
                .checkUsername(updatedFirstname);
    }

    @Test
    @ApiLogin(user = @GenerateUser(generateFirstname = true))
    @DisplayName("Имя пользователя может быть обновлено")
    void usernameUpdate(@User UserModel user) {
        String updatedFirstname = DataUtil.generateRandomFirstname();
        profilePage
                .checkUsername(user.getFirstname())
                .setUsername(updatedFirstname)
                .clickSave()
                .checkSnackbarMessage("Your profile is successfully updated");
        Selenide.refresh();
        profilePage
                .checkUsername(updatedFirstname);
    }

    @Test
    @ApiLogin
    @DisplayName("Имя пользователя не может быть длинней 50 символов")
    void usernameCanNotBeTooLong() {
        String updatedFirstname = DataUtil.generateStringWithLength(51);
        profilePage
                .checkUsername("")
                .setUsername(updatedFirstname)
                .clickSave()
                .checkFirstnameHelperHasMessage("First name length has to be not longer that 50 symbols");
        Selenide.refresh();
        profilePage
                .checkUsername("");
    }

    @Test
    @ApiLogin
    @DisplayName("Имя пользователя может быть длиной в 50 символов")
    void usernameCanBeLong() {
        String updatedFirstname = DataUtil.generateStringWithLength(50);
        profilePage
                .setUsername(updatedFirstname)
                .clickSave()
                .checkSnackbarMessage("Your profile is successfully updated");
        Selenide.refresh();
        profilePage
                .checkUsername(updatedFirstname);
    }
}
