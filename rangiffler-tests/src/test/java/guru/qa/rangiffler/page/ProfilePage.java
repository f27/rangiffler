package guru.qa.rangiffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.model.CountryEnum;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byTagAndText;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.rangiffler.selenide.CustomConditions.imageAsData;

public class ProfilePage extends BaseAuthorizedPage<ProfilePage> {

    private final SelenideElement firstnameInput = $("#firstname");
    private final SelenideElement lastnameInput = $("#surname");
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement countryCombobox = $("#location");
    private final SelenideElement countryInput = $("#select-location");
    private final SelenideElement countryListbox = $("ul[role=listbox]");
    private final SelenideElement resetButton = $(byTagAndText("button", "Reset"));
    private final SelenideElement saveButton = $("button[type=submit]");
    private final SelenideElement firstnameHelper = $("#firstname-helper-text");
    private final SelenideElement lastnameHelper = $("#surname-helper-text");
    private final SelenideElement avatarFileInput = $("#image__input");
    private final SelenideElement avatar = $("div.MuiAvatar-circular");
    private final SelenideElement avatarDefaultIcon = avatar.$("[data-testid=PersonIcon]");
    private final SelenideElement avatarImage = avatar.$("img");

    @Step("Нажать кнопку [Save]")
    public ProfilePage clickSave() {
        saveButton.click();
        return this;
    }

    @Step("Нажать кнопку [Reset]")
    public ProfilePage clickReset() {
        resetButton.click();
        return this;
    }

    @Step("Заполнить [First Name]")
    public ProfilePage setFirstname(String firstname) {
        firstnameInput.setValue(firstname);
        return this;
    }

    @Step("Заполнить [Surname]")
    public ProfilePage setLastname(String lastname) {
        lastnameInput.setValue(lastname);
        return this;
    }

    @Step("Проверить, что в поле [First Name] значение [{firstname}]")
    public ProfilePage checkFirstname(String firstname) {
        firstnameInput.shouldHave(exactValue(firstname));
        return this;
    }

    @Step("Проверить, что в поле [Surname] значение [{lastname}]")
    public ProfilePage checkLastname(String lastname) {
        lastnameInput.shouldHave(exactValue(lastname));
        return this;
    }

    @Step("Проверить, что в подсказке поля [First Name] сообщение [{msg}]")
    public ProfilePage checkFirstnameHelperHasMessage(String msg) {
        firstnameHelper.shouldHave(exactText(msg));
        return this;
    }

    @Step("Проверить, что в подсказке поля [Lastname] сообщение [{msg}]")
    public ProfilePage checkLastnameHelperHasMessage(String msg) {
        lastnameHelper.shouldHave(exactText(msg));
        return this;
    }

    @Step("Проверить, что поле [Username] отключено")
    public ProfilePage checkUsernameInputIsDisabled() {
        usernameInput.shouldHave(attribute("disabled"));
        return this;
    }

    @Step("Проверить, что поле [Username] содержит [{username}]")
    public ProfilePage checkUsernameInputValue(String username) {
        usernameInput.shouldHave(value(username));
        return this;
    }

    @Step("Проверить, что поле [Location] содержит [{country}]")
    public ProfilePage checkCountry(CountryEnum country) {
        countryCombobox.shouldHave(exactOwnTextCaseSensitive(country.toString()));
        countryInput.shouldHave(exactValue(country.getCode()));
        return this;
    }

    @Step("Выбрать [Location]")
    public ProfilePage selectCountry(CountryEnum country) {
        countryCombobox.click();
        countryListbox.$$("li").findBy(attribute("data-value", country.getCode())).click();
        return this;
    }

    @Step("Проверить, что в [Avatar] отображается иконка по умолчанию")
    public ProfilePage checkAvatarHasDefaultIcon() {
        avatarDefaultIcon.shouldBe(visible);
        return this;
    }

    @Step("Проверить, что в [Avatar] отображается правильная картинка")
    public ProfilePage checkAvatar(String imageClassPath) {
        avatarImage.shouldHave(imageAsData(imageClassPath));
        return this;
    }

    @Step("Загрузить [Avatar]")
    public ProfilePage uploadAvatar(String imageClassPath) {
        avatarFileInput.uploadFromClasspath(imageClassPath);
        return this;
    }
}
