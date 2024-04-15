package guru.qa.rangiffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byTagAndText;
import static com.codeborne.selenide.Selenide.$;

public class WelcomePage extends BasePage<WelcomePage> {
    private final SelenideElement loginButton = $(byTagAndText("button", "Login"));
    private final SelenideElement registerButton = $(byTagAndText("a", "Register"));

    @Step("Нажать кнопку [Login]")
    public void clickLoginButton() {
        loginButton.click();
    }

    @Step("Нажать кнопку [Register]")
    public void clickRegisterButton() {
        registerButton.click();
    }

    @Step("Проверить, что видно кнопки [Login] и [Register]")
    public WelcomePage checkLoginAndRegisterButtonsAreVisible() {
        loginButton.shouldBe(visible);
        registerButton.shouldBe(visible);
        return this;
    }
}
