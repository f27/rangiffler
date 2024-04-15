package guru.qa.rangiffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selectors.byTagAndText;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage extends BasePage<RegisterPage> {

    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement passwordSubmitInput = $("#passwordSubmit");
    private final SelenideElement signUpButton = $("button[type=submit]");
    private final SelenideElement usernameHelper = usernameInput.closest("label").$(".form__error");
    private final SelenideElement passwordHelper = passwordInput.closest("label").$(".form__error");
    private final SelenideElement successfulRegistrationMessage = $(".form__paragraph_success");
    private final SelenideElement signInButton = $(byTagAndText("a", "Sign in!"));

    @Step("Заполнить [Username]")
    public RegisterPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Step("Заполнить [Password]")
    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Заполнить [Password Submit]")
    public RegisterPage setPasswordSubmit(String password) {
        passwordSubmitInput.setValue(password);
        return this;
    }

    @Step("Нажать кнопку [Sign up]")
    public void clickSignUp() {
        signUpButton.click();
    }

    @Step("Проверить, что под полем [Username] появилась ошибка")
    public RegisterPage checkUsernameHelperHasError(String errorMsg) {
        usernameHelper.shouldHave(exactText(errorMsg));
        return this;
    }

    @Step("Проверить, что под полем [Password] ошибка")
    public RegisterPage checkPasswordHelperHasError(String errorMsg) {
        passwordHelper.shouldHave(exactText(errorMsg));
        return this;
    }

    @Step("Проверить, что появилась надпись об успешной регистрации")
    public RegisterPage checkSuccessfulRegistrationMessageExist(String msg) {
        successfulRegistrationMessage.shouldHave(exactText(msg));
        return this;
    }

    @Step("Нажать кнопку [Sign in]")
    public void clickSignIn() {
        signInButton.click();
    }
}
