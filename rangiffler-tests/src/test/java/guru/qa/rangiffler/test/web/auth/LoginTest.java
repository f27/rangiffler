package guru.qa.rangiffler.test.web.auth;

import com.codeborne.selenide.Selenide;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.web.BaseWebTest;
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
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
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
                .setUsername(user.getUsername())
                .setPassword(user.getPassword() + "1")
                .clickSignIn();
        loginPage
                .checkFormHasErrorBadCredentials();
    }

    @Test
    @GenerateUser
    @GenerateUser
    @DisplayName("Нельзя войти с паролем другого пользователя")
    void otherUserPasswordTest(@User(FOR_GENERATE_USER) UserModel[] users) {
        Selenide.open("/");
        welcomePage.clickLoginButton();
        loginPage
                .setUsername(users[0].getUsername())
                .setPassword(users[1].getPassword())
                .clickSignIn();
        loginPage
                .checkFormHasErrorBadCredentials();
    }
}