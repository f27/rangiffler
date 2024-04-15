package guru.qa.rangiffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.model.CountryEnum;
import guru.qa.rangiffler.model.PhotoModel;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class AddPhotoModal extends BaseComponent<AddPhotoModal> {
    private final SelenideElement photoInput = self.$("#image__input");
    private final SelenideElement countryCombobox = self.$("#country");
    private final SelenideElement countryMenu = $("#menu-country");
    private final SelenideElement countryListbox = countryMenu.$("ul[role=listbox]");
    private final SelenideElement descriptionTextarea = self.$("#description");
    private final SelenideElement saveButton = self.$("button[type=submit]");
    private final SelenideElement descriptionHelper = self.$("#description-helper-text");
    private final SelenideElement imageHelper = self.$("#image-helper-text");

    public AddPhotoModal() {
        super($("div.MuiModal-root"));
    }

    @Step("[ADD PHOTO MODAL] Загрузить фотографию")
    public AddPhotoModal uploadPhoto(PhotoModel photo) {
        photoInput.uploadFromClasspath(photo.photo());
        countryCombobox.click();
        countryListbox.$$("li").findBy(attribute("data-value", photo.country().getCode()))
                .scrollIntoView(true)
                .shouldNotBe(animated)
                .click();
        descriptionTextarea.click();
        descriptionTextarea.setValue(photo.description());
        return this;
    }

    @Step("[ADD PHOTO MODAL] Указать картинку")
    public AddPhotoModal setImage(String imageClassPath) {
        photoInput.uploadFromClasspath(imageClassPath);
        return this;
    }

    @Step("[ADD PHOTO MODAL] Выбрать страну [{country}]")
    public AddPhotoModal selectCountry(CountryEnum country) {
        countryCombobox.click();
        countryListbox.$$("li").findBy(attribute("data-value", country.getCode()))
                .scrollIntoView(true)
                .shouldNotBe(animated)
                .click();
        return this;
    }

    @Step("[ADD PHOTO MODAL] Указать описание [{description}]")
    public AddPhotoModal setDescription(String description) {
        descriptionTextarea.click();
        descriptionTextarea.setValue(description);
        return this;
    }

    @Step("[ADD PHOTO MODAL] Нажать кнопку [Save]")
    public AddPhotoModal clickSave() {
        saveButton.click();
        return this;
    }

    @Step("[ADD PHOTO MODAL] Проверить, что для картинки есть ошибка")
    public AddPhotoModal checkImageHasError(String msg) {
        imageHelper.shouldHave(exactText(msg));
        return this;
    }

    @Step("[ADD PHOTO MODAL] Проверить, что для описания есть ошибка")
    public AddPhotoModal checkDescriptionHasError(String msg) {
        descriptionHelper.shouldHave(exactText(msg));
        return this;
    }
}
