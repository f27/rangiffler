package guru.qa.rangiffler.test.web.auth;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.test.web.BaseWebTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Feature("Авторизация")
@Story("Выход из аккаунта")
@DisplayName("Выход из аккаунта")
public class LogoutTest extends BaseWebTest {

    @Test
    @ApiLogin
    @DisplayName("Успешный выход из аккаунта")
    void logoutTest() {
        myTravelsPage
                .clickLogout();
        welcomePage
                .checkSnackbarMessage("Session completed")
                .checkLoginAndRegisterButtonsAreVisible();
    }
}
