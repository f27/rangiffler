package guru.qa.rangiffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byTagAndText;
import static com.codeborne.selenide.Selenide.$;

public class ProfilePage extends BaseAuthorizedPage<ProfilePage> {

    private final SelenideElement firstnameInput = $("#firstname");
    private final SelenideElement lastnameInput = $("#surname");
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement countryCombobox = $("#location");
    private final SelenideElement resetButton = $(byTagAndText("button", "Reset"));
    private final SelenideElement saveButton = $("button[type=submit]");
    private final SelenideElement firstnameHelper = $("#firstname-helper-text");
    private final SelenideElement lastnameHelper = $("#surname-helper-text");

    @Step("Нажать кнопку [Save]")
    public ProfilePage clickSave() {
        saveButton.click();
        return this;
    }

    @Step("Нажать кнопку [Reset]")
    public ProfilePage clickReset() {
        saveButton.click();
        return this;
    }

    @Step("Заполнить [First Name]")
    public ProfilePage setUsername(String firstname) {
        firstnameInput.setValue(firstname);
        return this;
    }

    @Step("Заполнить [Surname]")
    public ProfilePage setLastname(String lastname) {
        lastnameInput.setValue(lastname);
        return this;
    }

    @Step("Проверить, что в поле [First Name] значение [{firstname}]")
    public ProfilePage checkUsername(String firstname) {
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

}
