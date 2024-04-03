package guru.qa.rangiffler.page;

import guru.qa.rangiffler.page.component.SnackbarComponent;
import io.qameta.allure.Step;

public abstract class BasePage<T extends BasePage<T>> {

    protected final SnackbarComponent snackbarComponent = new SnackbarComponent();

    @SuppressWarnings("unchecked")
    @Step("Проверить, что появилось сообщение [{msg}]")
    public T checkSnackbarMessage(String msg) {
        snackbarComponent.messageShouldHaveText(msg);
        return (T) this;
    }

}
