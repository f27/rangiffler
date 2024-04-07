package guru.qa.rangiffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.model.PhotoModel;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.animated;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.$;

public class AddPhotoModal extends BaseComponent<AddPhotoModal> {
    private final SelenideElement photoInput = $("#image__input");
    private final SelenideElement countryCombobox = $("#country");
    private final SelenideElement countryListbox = $("ul[role=listbox]");
    private final SelenideElement descriptionTextarea = $("#description");
    private final SelenideElement saveButton = $("button[type=submit]");
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
        descriptionTextarea.setValue(photo.description());
        return this;
    }

    @Step("[ADD PHOTO MODAL] Нажать кнопку [Save]")
    public void clickSave() {
        saveButton.click();
    }
}
