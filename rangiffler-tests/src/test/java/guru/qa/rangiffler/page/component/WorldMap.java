package guru.qa.rangiffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.model.CountryEnum;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.cssValue;
import static com.codeborne.selenide.Condition.exactOwnText;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.rangiffler.selenide.CustomConditions.cssValueGreaterThan;

public class WorldMap extends BaseComponent<WorldMap> {

    private final SelenideElement title = self.$(".worldmap__figure-caption");

    public WorldMap() {
        super($(".worldmap__figure-container"));
    }

    private SelenideElement countryElement(String countryCode) {
        return self.$("a#" + countryCode.toUpperCase());
    }

    @Step("[WORLD MAP] Выбрать страну [{country}]")
    public WorldMap clickCountry(CountryEnum country) {
        countryElement(country.getCode()).click();
        return this;
    }

    @Step("[WORLD MAP] Проверить, что выбрана страна [{country}]")
    public WorldMap checkSelectedCountry(CountryEnum country) {
        title.shouldHave(exactOwnText(country.toString()));
        return this;
    }

    @Step("[WORLD MAP] Проверить, что выбраны все страны")
    public WorldMap checkSelectedAllCountries() {
        title.shouldHave(exactOwnText("All countries"));
        return this;
    }

    @Step("[WORLD MAP] Проверить, что страна подсвечена")
    public WorldMap checkCountryIsHighlighted(CountryEnum country) {
        countryElement(country.getCode())
                .$("path").shouldHave(cssValueGreaterThan("fill-opacity", 0.0));
        return this;
    }

    @Step("[WORLD MAP] Проверить, что страна не подсвечена")
    public WorldMap checkCountryIsNotHighlighted(CountryEnum country) {
        countryElement(country.getCode())
                .$("path").shouldHave(cssValue("fill-opacity", "0"));
        return this;
    }
}
