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
                .getDrawer().clickProfile();
    }

    @Test
    @ApiLogin
    @DisplayName("Имя пользователя можно заполнить")
    void firstSetFirstnameTest() {
        String updatedFirstname = DataUtil.generateRandomFirstname();
        profilePage
                .checkFirstname("")
                .setFirstname(updatedFirstname)
                .clickSave()
                .getSnackbar().messageShouldHaveText("Your profile is successfully updated");
        Selenide.refresh();
        profilePage
                .checkFirstname(updatedFirstname);
    }

    @Test
    @ApiLogin(user = @GenerateUser(generateFirstname = true))
    @DisplayName("Имя пользователя может быть обновлено")
    void firstnameUpdateTest(@User UserModel user) {
        String updatedFirstname = DataUtil.generateRandomFirstname();
        profilePage
                .checkFirstname(user.firstname())
                .setFirstname(updatedFirstname)
                .clickSave()
                .getSnackbar().messageShouldHaveText("Your profile is successfully updated");
        Selenide.refresh();
        profilePage
                .checkFirstname(updatedFirstname);
    }

    @Test
    @ApiLogin
    @DisplayName("Имя пользователя не может быть длинней 50 символов")
    void firstnameCanNotBeTooLongTest() {
        String updatedFirstname = DataUtil.generateStringWithLength(51);
        profilePage
                .checkFirstname("")
                .setFirstname(updatedFirstname)
                .clickSave()
                .checkFirstnameHelperHasMessage("First name length has to be not longer that 50 symbols");
        Selenide.refresh();
        profilePage
                .checkFirstname("");
    }

    @Test
    @ApiLogin
    @DisplayName("Имя пользователя может быть длиной в 50 символов")
    void firstnameCanBeLongTest() {
        String updatedFirstname = DataUtil.generateStringWithLength(50);
        profilePage
                .setFirstname(updatedFirstname)
                .clickSave()
                .getSnackbar().messageShouldHaveText("Your profile is successfully updated");
        Selenide.refresh();
        profilePage
                .checkFirstname(updatedFirstname);
    }

    @Test
    @ApiLogin(user = @GenerateUser(generateFirstname = true))
    @DisplayName("Имя пользователя по умолчанию должна быть его")
    void firstnameShouldHaveUsersValueTest(@User UserModel user) {
        profilePage
                .checkFirstname(user.firstname());
    }

    @Test
    @ApiLogin(user = @GenerateUser(generateFirstname = true))
    @DisplayName("Имя пользователя, сохраненное заранее, должно восстановиться при нажатии [Reset]")
    void firstnameWithValueShouldBeRestoredAfterResetButtonClickedTest(@User UserModel user) {
        String updatedFirstname = DataUtil.generateRandomFirstname();
        profilePage
                .checkFirstname(user.firstname())
                .setFirstname(updatedFirstname)
                .checkFirstname(updatedFirstname)
                .clickReset()
                .checkFirstname(user.firstname());
    }

    @Test
    @ApiLogin
    @DisplayName("Имя пользователя, которое было пустым, должно быть пустым после нажатия [Reset]")
    void emptyFirstnameShouldBeEmptyAfterResetButtonClickedTest() {
        String updatedFirstname = DataUtil.generateRandomFirstname();
        profilePage
                .checkFirstname("")
                .setFirstname(updatedFirstname)
                .checkFirstname(updatedFirstname)
                .clickReset()
                .checkFirstname("");
    }

    @Test
    @ApiLogin(user = @GenerateUser(generateFirstname = true))
    @DisplayName("Имя пользователя может быть обновлено после нажатия кнопки [Reset]")
    void firstnameUpdateAfterResetButtonClickedTest(@User UserModel user) {
        String updatedFirstname = DataUtil.generateRandomFirstname();
        profilePage
                .checkFirstname(user.firstname())
                .setFirstname(updatedFirstname)
                .checkFirstname(updatedFirstname)
                .clickReset()
                .setFirstname(updatedFirstname)
                .checkFirstname(updatedFirstname)
                .clickSave()
                .getSnackbar().messageShouldHaveText("Your profile is successfully updated");
        Selenide.refresh();
        profilePage
                .checkFirstname(updatedFirstname);
    }
}
