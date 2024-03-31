package guru.qa.rangiffler.page;

import guru.qa.rangiffler.page.component.HeaderComponent;

public abstract class BaseAuthorizedPage<T extends BaseAuthorizedPage<T>> extends BasePage<T> {

    protected final HeaderComponent headerComponent = new HeaderComponent();

}
