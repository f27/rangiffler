package guru.qa.rangiffler.page;

import io.qameta.allure.Step;

public class MyTravelsPage extends BaseAuthorizedPage<MyTravelsPage> {

    @Step("Проверить, что успешно авторизовались")
    public void checkSuccessfullyAuthorized() {
        headerComponent.logoutButtonShouldBeVisible();
    }
}
