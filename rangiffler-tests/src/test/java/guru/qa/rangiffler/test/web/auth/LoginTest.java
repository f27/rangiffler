package guru.qa.rangiffler.test.web.auth;

import com.codeborne.selenide.Selenide;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.web.BaseWebTest;
import guru.qa.rangiffler.util.DataUtil;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.rangiffler.jupiter.annotation.User.GenerationType.FOR_GENERATE_USER;

@Feature("Авторизация")
@Story("Вход в аккаунт")
@DisplayName("Вход в аккаунт")
public class LoginTest extends BaseWebTest {

    @Test
    @GenerateUser
    @DisplayName("Успешный вход")
    void successfulLoginTest(@User(FOR_GENERATE_USER) UserModel user) {
        Selenide.open("/");
        welcomePage.clickLoginButton();
        loginPage
                .setUsername(user.username())
                .setPassword(user.password())
                .clickSignIn();
        myTravelsPage
                .checkSuccessfullyAuthorized();
    }

    @Test
    @GenerateUser
    @DisplayName("Нельзя войти с неправильным паролем")
    void wrongPasswordTest(@User(FOR_GENERATE_USER) UserModel user) {
        Selenide.open("/");
        welcomePage.clickLoginButton();
        loginPage
                .setUsername(user.username())
                .setPassword(user.password() + "1")
                .clickSignIn();
        loginPage
                .checkFormHasError("Неверные учетные данные пользователя");
    }

    @Test
    @GenerateUser
    @GenerateUser
    @DisplayName("Нельзя войти с паролем другого пользователя")
    void otherUserPasswordTest(@User(FOR_GENERATE_USER) UserModel[] users) {
        Selenide.open("/");
        welcomePage.clickLoginButton();
        loginPage
                .setUsername(users[0].username())
                .setPassword(users[1].password())
                .clickSignIn();
        loginPage
                .checkFormHasError("Неверные учетные данные пользователя");
    }

    @Test
    @DisplayName("Нельзя войти незарегистрированным пользователем")
    void shouldNotLoginWithNotRegisteredUserTest() {
        Selenide.open("/");
        welcomePage.clickLoginButton();
        loginPage
                .setUsername(DataUtil.generateRandomUsername())
                .setPassword(DataUtil.generateRandomPassword())
                .clickSignIn();
        loginPage
                .checkFormHasError("Неверные учетные данные пользователя");
    }

    @Test
    @ApiLogin
    @DisplayName("Если истек access_token, то он обновится с помощью refresh_token")
    void accessTokenCanBeRefreshedTest() {
        Selenide.localStorage().removeItem("access_token");
        Selenide.refresh();
        myTravelsPage
                .checkSuccessfullyAuthorized();
    }
}
