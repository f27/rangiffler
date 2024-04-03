package guru.qa.rangiffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class HeaderComponent extends BaseComponent<HeaderComponent> {

    public HeaderComponent() {
        super($("header"));
    }

    private final SelenideElement logoutButton = self.$("button[aria-label=Logout]");

    @Step("[HEADER] Проверить, что видно кнопку [Logout]")
    public void logoutButtonShouldBeVisible() {
        logoutButton.shouldBe(visible);
    }

    @Step("[HEADER] Нажать кнопку [Logout]")
    public void clickLogout() {
        logoutButton.click();
    }
}
