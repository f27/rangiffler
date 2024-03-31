package guru.qa.rangiffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selectors.byTagAndText;
import static com.codeborne.selenide.Selenide.$;

public class WelcomePage extends BasePage<WelcomePage> {
    private final SelenideElement loginButton = $(byTagAndText("button", "Login"));
    private final SelenideElement registerButton = $(byTagAndText("a", "Register"));

    @Step("Нажать кнопку [Login]")
    public void clickLoginButton() {
        loginButton.click();
    }
}
