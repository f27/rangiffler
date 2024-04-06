package guru.qa.rangiffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;

public class Snackbar extends BaseComponent<Snackbar> {
    public Snackbar() {
        super($("div.MuiSnackbar-root"));
    }

    private final SelenideElement message = self.$(".MuiAlert-message");

    @Step("[SNACKBAR] Проверить, что появилось сообщение")
    public void messageShouldHaveText(String msg) {
        message.shouldHave(exactText(msg));
    }
}
