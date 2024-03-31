package guru.qa.rangiffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {
    private final SelenideElement usernameInput = $(byName("username"));
    private final SelenideElement passwordInput = $(byName("password"));
    private final SelenideElement signInButton = $("button[type=submit]");

    @Step("Заполнить имя пользователя")
    public LoginPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Step("Заполнить пароль")
    public LoginPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Нажать кнопку [Sign in]")
    public void clickSignIn() {
        signInButton.click();
    }
}
