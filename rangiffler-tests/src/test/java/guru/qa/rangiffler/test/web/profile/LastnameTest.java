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
@Story("Фамилия")
@DisplayName("Фамилия")
public class LastnameTest extends BaseWebTest {

    @BeforeEach
    void openProfile() {
        myTravelsPage
                .goToProfile();
    }

    @Test
    @ApiLogin
    @DisplayName("Фамилия может быть заполнена")
    void firstSetLastname() {
        String updatedLastname = DataUtil.generateRandomLastname();
        profilePage
                .checkLastname("")
                .setLastname(updatedLastname)
                .clickSave()
                .checkSnackbarMessage("Your profile is successfully updated");
        Selenide.refresh();
        profilePage
                .checkLastname(updatedLastname);
    }

    @Test
    @ApiLogin(user = @GenerateUser(generateLastname = true))
    @DisplayName("Фамилия может быть обновлена")
    void lastnameUpdate(@User UserModel user) {
        String updatedLastname = DataUtil.generateRandomLastname();
        profilePage
                .checkLastname(user.getLastname())
                .setLastname(updatedLastname)
                .clickSave()
                .checkSnackbarMessage("Your profile is successfully updated");
        Selenide.refresh();
        profilePage
                .checkLastname(updatedLastname);
    }

    @Test
    @ApiLogin
    @DisplayName("Фамилия не может быть длинней 50 символов")
    void lastnameCanNotBeTooLong() {
        String updatedLastname = DataUtil.generateStringWithLength(51);
        profilePage
                .checkLastname("")
                .setLastname(updatedLastname)
                .clickSave()
                .checkLastnameHelperHasMessage("Surname length has to be not longer that 50 symbols");
        Selenide.refresh();
        profilePage
                .checkLastname("");
    }

    @Test
    @ApiLogin
    @DisplayName("Фамилия может быть длиной в 50 символов")
    void lastnameCanBeLong() {
        String updatedLastname = DataUtil.generateStringWithLength(50);
        profilePage
                .setLastname(updatedLastname)
                .clickSave()
                .checkSnackbarMessage("Your profile is successfully updated");
        Selenide.refresh();
        profilePage
                .checkLastname(updatedLastname);
    }
}
