package guru.qa.rangiffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.model.CountryEnum;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.animated;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.$;

public class EditPhotoModal extends BaseComponent<EditPhotoModal> {

    private final SelenideElement countryCombobox = $("#country");
    private final SelenideElement countryListbox = $("ul[role=listbox]");
    private final SelenideElement descriptionTextarea = $("#description");
    private final SelenideElement saveButton = $("button[type=submit]");

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
    public void clickSave() {
        saveButton.click();
    }
}
