package guru.qa.rangiffler.page.component;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

public abstract class BaseComponent<T extends BaseComponent<T>> {

    @Getter
    protected final SelenideElement self;

    public BaseComponent(SelenideElement self) {
        this.self = self;
    }
}
