package guru.qa.rangiffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class DrawerComponent extends BaseComponent<DrawerComponent> {
    public DrawerComponent() {
        super($("div.MuiDrawer-root"));
    }

    private final SelenideElement expandDrawerButton = self.$("button[aria-label='open drawer']");
    private final SelenideElement profileButton = self.$("a[href='/profile']");
    private final SelenideElement myTravelButton = self.$("a[href='/my-travel']");
    private final SelenideElement peopleButton = self.$("a[href='/people']");

    @Step("[DRAWER] Нажать кнопку профиль")
    public void clickProfile() {
        profileButton.click();
    }
}
