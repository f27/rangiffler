package guru.qa.rangiffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class Drawer extends BaseComponent<Drawer> {
    private final SelenideElement expandDrawerButton = self.$("button[aria-label='open drawer']");
    private final SelenideElement profileButton = self.$("a[href='/profile']");
    private final SelenideElement myTravelButton = self.$("a[href='/my-travel']");
    private final SelenideElement peopleButton = self.$("a[href='/people']");

    public Drawer() {
        super($("div.MuiDrawer-root"));
    }

    @Step("[DRAWER] Нажать кнопку [Profile]")
    public void clickProfile() {
        profileButton.click();
    }

    @Step("[DRAWER] Нажать кнопку [My map]")
    public void clickMyMap() {
        myTravelButton.click();
    }

    @Step("[DRAWER] Нажать кнопку [People]")
    public void clickPeople() {
        peopleButton.click();
    }
}
