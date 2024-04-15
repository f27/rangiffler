package guru.qa.rangiffler.test.web.profile;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.web.BaseWebTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Feature("Профиль аккаунта")
@Story("Username")
@DisplayName("Username")
public class UsernameTest extends BaseWebTest {

    @BeforeEach
    void openProfile() {
        myTravelsPage
                .getDrawer().clickProfile();
    }

    @Test
    @ApiLogin
    @DisplayName("Username должно быть отключенным и содержать значение")
    void usernameInputShouldBeDisabled(@User UserModel user) {
        profilePage
                .checkUsernameInputIsDisabled()
                .checkUsernameInputValue(user.username());
    }
}
