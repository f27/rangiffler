package guru.qa.rangiffler.test.web.profile;

import com.codeborne.selenide.Selenide;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.CountryEnum;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.web.BaseWebTest;
import guru.qa.rangiffler.util.DataUtil;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Feature("Профиль аккаунта")
@Story("Страна пользователя")
@DisplayName("Страна пользователя")
public class CountryTest extends BaseWebTest {

    @BeforeEach
    void openProfile() {
        myTravelsPage
                .getDrawer().clickProfile();
    }

    @Test
    @ApiLogin(user = @GenerateUser(generateCountry = true))
    @DisplayName("Страна пользователя быть из профиля")
    void countryShouldHaveUsersValueTest(@User UserModel user) {
        profilePage
                .checkCountry(user.country());
    }

    @Test
    @ApiLogin
    @DisplayName("Страна пользователя по умолчанию должна быть [Russian Federation]")
    void countryShouldHaveDefaultValueTest() {
        profilePage
                .checkCountry(CountryEnum.RUSSIAN_FEDERATION);
    }

    @Test
    @ApiLogin
    @DisplayName("Страна пользователя может быть обновлена")
    void countryUpdateTest(@User UserModel user) {
        CountryEnum updatedCountry = DataUtil.generateRandomCountry();
        profilePage
                .checkCountry(user.country())
                .selectCountry(updatedCountry)
                .checkCountry(updatedCountry)
                .clickSave()
                .getSnackbar().messageShouldHaveText("Your profile is successfully updated");
        Selenide.refresh();
        profilePage
                .checkCountry(updatedCountry);
    }

    @Test
    @ApiLogin
    @DisplayName("Страна пользователя должна восстановиться при нажатии [Reset]")
    void countryShouldBeRestoredAfterResetButtonClickedTest(@User UserModel user) {
        CountryEnum updatedCountry = DataUtil.generateRandomCountry();
        profilePage
                .checkCountry(user.country())
                .selectCountry(updatedCountry)
                .checkCountry(updatedCountry)
                .clickReset()
                .checkCountry(user.country());
    }

    @Test
    @ApiLogin
    @DisplayName("Страна пользователя может быть обновлена после нажатия кнопки [Reset]")
    void countryUpdateAfterResetButtonClickedTest(@User UserModel user) {
        CountryEnum updatedCountry = DataUtil.generateRandomCountry();
        profilePage
                .checkCountry(user.country())
                .selectCountry(updatedCountry)
                .checkCountry(updatedCountry)
                .clickReset()
                .selectCountry(updatedCountry)
                .checkCountry(updatedCountry)
                .clickSave()
                .getSnackbar().messageShouldHaveText("Your profile is successfully updated");
        Selenide.refresh();
        profilePage
                .checkCountry(updatedCountry);
    }
}
