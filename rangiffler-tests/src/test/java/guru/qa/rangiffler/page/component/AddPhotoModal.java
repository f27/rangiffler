package guru.qa.rangiffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class AddPhotoModal extends BaseComponent<AddPhotoModal> {
    public AddPhotoModal() {
        super($("div.MuiModal-root"));
    }

    private final SelenideElement photoInput = $("#image__input");
    private final SelenideElement saveButton = $("button[type=submit]");

    @Step("[ADD PHOTO MODAL] Загрузить фотографию")
    public AddPhotoModal uploadPhoto(String imageClasspath) {
        photoInput.uploadFromClasspath(imageClasspath);
        return this;
    }

    @Step("[ADD PHOTO MODAL] Нажать кнопку [Save]")
    public void clickSave() {
        saveButton.click();
    }
}
