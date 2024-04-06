package guru.qa.rangiffler.test.web.auth;

import com.codeborne.selenide.Selenide;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.annotation.UserForRegistration;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.web.BaseWebTest;
import guru.qa.rangiffler.util.DataUtil;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.rangiffler.jupiter.annotation.User.GenerationType.FOR_GENERATE_USER;

@Feature("Авторизация")
@Story("Регистрация")
@DisplayName("Регистрация")
public class RegisterTest extends BaseWebTest {

    @BeforeEach
    void openRegistrationPage() {
        Selenide.open("/");
        welcomePage
                .clickRegisterButton();
    }

    @Test
    @GenerateUser
    @DisplayName("Нельзя зарегистрироваться с уже существующим именем пользователя")
    void shouldNotRegisterUserWithSameUsernameTest(@User(FOR_GENERATE_USER) UserModel user) {
        registerPage
                .setUsername(user.username())
                .setPassword(user.password())
                .setPasswordSubmit(user.password())
                .clickSignUp();
        registerPage
                .checkUsernameHelperHasError(String.format("Username `%s` already exists", user.username()));
    }

    @Test
    @DisplayName("Нельзя зарегистрироваться со слишком коротким паролем")
    void shouldNotRegisterUserWithTooShortPasswordTest() {
        String shortPassword = DataUtil.generateRandomPassword(1, 2);
        registerPage
                .setUsername(DataUtil.generateRandomUsername())
                .setPassword(shortPassword)
                .setPasswordSubmit(shortPassword)
                .clickSignUp();
        registerPage
                .checkPasswordHelperHasError("Allowed password length should be from 3 to 12 characters");
    }

    @Test
    @DisplayName("Нельзя зарегистрироваться со слишком коротким паролем")
    void shouldNotRegisterUserWithTooLongPasswordTest() {
        String longPassword = DataUtil.generateRandomPassword(13, 20);
        registerPage
                .setUsername(DataUtil.generateRandomUsername())
                .setPassword(longPassword)
                .setPasswordSubmit(longPassword)
                .clickSignUp();
        registerPage
                .checkPasswordHelperHasError("Allowed password length should be from 3 to 12 characters");
    }

    @Test
    @DisplayName("Нельзя зарегистрироваться с неверным подтверждением пароля")
    void shouldNotRegisterUserWithWrongPasswordSubmitTest() {
        registerPage
                .setUsername(DataUtil.generateRandomUsername())
                .setPassword(DataUtil.generateRandomPassword())
                .setPasswordSubmit(DataUtil.generateRandomPassword())
                .clickSignUp();
        registerPage
                .checkPasswordHelperHasError("Passwords should be equal");
    }

    @Test
    @DisplayName("Нельзя зарегистрироваться со слишком длинным именем пользователя")
    void shouldNotRegisterUserWithTooLongUsernameTest() {
        String password = DataUtil.generateRandomPassword();
        registerPage
                .setUsername(DataUtil.generateStringWithLength(51))
                .setPassword(password)
                .setPasswordSubmit(password)
                .clickSignUp();
        registerPage
                .checkUsernameHelperHasError("Username can`t be longer than 50 characters");
    }

    @Test
    @DisplayName("Успешная регистрация")
    void successfulRegistrationTest(@UserForRegistration UserModel user) {
        registerPage
                .setUsername(user.username())
                .setPassword(user.password())
                .setPasswordSubmit(user.password())
                .clickSignUp();
        registerPage
                .checkSuccessfulRegistrationMessageExist("Congratulations! You've registered!")
                .clickSignIn();
        loginPage
                .setUsername(user.username())
                .setPassword(user.password())
                .clickSignIn();
        myTravelsPage
                .checkSuccessfullyAuthorized();
    }
}
