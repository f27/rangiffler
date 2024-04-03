package guru.qa.rangiffler.page;

import guru.qa.rangiffler.page.component.HeaderComponent;
import io.qameta.allure.Step;

public abstract class BaseAuthorizedPage<T extends BaseAuthorizedPage<T>> extends BasePage<T> {

    protected final HeaderComponent headerComponent = new HeaderComponent();

    @Step("Выйти из аккаунта")
    public void clickLogout() {
        headerComponent.clickLogout();
    }

}
