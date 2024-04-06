package guru.qa.rangiffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.page.component.AddPhotoModal;
import guru.qa.rangiffler.util.ImageUtil;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selectors.byTagAndText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MyTravelsPage extends BaseAuthorizedPage<MyTravelsPage> {

    @Getter
    private final AddPhotoModal addPhotoModal = new AddPhotoModal();

    private final SelenideElement addPhotoButton = $(byTagAndText("button", "Add photo"));
    private final ElementsCollection photosCollection = $$(".photo-card__image");

    @Step("Проверить, что успешно авторизовались")
    public void checkSuccessfullyAuthorized() {
        header.logoutButtonShouldBeVisible();
    }

    @Step("Нажать кнопку [Add photo]")
    public MyTravelsPage clickAddPhoto() {
        addPhotoButton.click();
        return this;
    }

    @Step("Проверить, что добавлено [{amount}] фотографий")
    public MyTravelsPage checkAmountOfPhotos(int amount) {
        photosCollection.shouldHave(size(amount));
        return this;
    }

    @Step("Проверить, что фотография видна")
    public MyTravelsPage checkPhotoIsVisible(String imageClasspath) {
        photosCollection.get(0).shouldHave(attribute("src", ImageUtil.getImageAsBase64(imageClasspath)));
        return this;
    }
}
