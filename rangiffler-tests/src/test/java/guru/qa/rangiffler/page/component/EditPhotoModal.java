package guru.qa.rangiffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.model.CountryEnum;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class EditPhotoModal extends BaseComponent<EditPhotoModal> {

    private final SelenideElement countryCombobox = self.$("#country");
    private final SelenideElement countryListbox = self.$("ul[role=listbox]");
    private final SelenideElement descriptionTextarea = self.$("#description");
    private final SelenideElement saveButton = self.$("button[type=submit]");
    private final SelenideElement descriptionHelper = self.$("#description-helper-text");

    public EditPhotoModal() {
        super($("div.MuiModal-root"));
    }

    @Step("[EDIT PHOTO MODAL] Выбрать страну [{country}]")
    public EditPhotoModal selectCountry(CountryEnum country) {
        countryCombobox.click();
        countryListbox.$$("li").findBy(attribute("data-value", country.getCode()))
                .scrollIntoView(true)
                .shouldNotBe(animated)
                .click();
        return this;
    }

    @Step("[EDIT PHOTO MODAL] Указать описание [{description}]")
    public EditPhotoModal setDescription(String description) {
        descriptionTextarea.click();
        descriptionTextarea.setValue(description);
        return this;
    }


    @Step("[EDIT PHOTO MODAL] Нажать кнопку [Save]")
    public EditPhotoModal clickSave() {
        saveButton.click();
        return this;
    }

    @Step("[EDIT PHOTO MODAL] Проверить, что для описания есть ошибка")
    public EditPhotoModal checkDescriptionHasError(String msg) {
        descriptionHelper.shouldHave(exactText(msg));
        return this;
    }
}
