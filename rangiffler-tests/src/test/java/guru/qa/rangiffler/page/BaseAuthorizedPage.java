package guru.qa.rangiffler.page;

import guru.qa.rangiffler.page.component.Drawer;
import guru.qa.rangiffler.page.component.Header;
import lombok.Getter;

public abstract class BaseAuthorizedPage<T extends BaseAuthorizedPage<T>> extends BasePage<T> {

    @Getter
    protected final Header header = new Header();
    @Getter
    protected final Drawer drawer = new Drawer();

}
