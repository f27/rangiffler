package guru.qa.rangiffler.page;

import guru.qa.rangiffler.page.component.Snackbar;
import lombok.Getter;

public abstract class BasePage<T extends BasePage<T>> {

    @Getter
    protected final Snackbar snackbar = new Snackbar();

}
