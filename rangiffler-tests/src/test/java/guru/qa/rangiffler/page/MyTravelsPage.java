package guru.qa.rangiffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.model.PhotoModel;
import guru.qa.rangiffler.page.component.AddPhotoModal;
import io.qameta.allure.Step;
import lombok.Getter;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Selectors.byTagAndText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static guru.qa.rangiffler.selenide.listener.CustomElementsConditions.exactPhotoCards;

public class MyTravelsPage extends BaseAuthorizedPage<MyTravelsPage> {

    @Getter
    private final AddPhotoModal addPhotoModal = new AddPhotoModal();

    private final SelenideElement addPhotoButton = $(byTagAndText("button", "Add photo"));
    private final ElementsCollection photoCardsCollection = $$("[data-testid=photoCard]");

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
        photoCardsCollection.shouldHave(size(amount));
        return this;
    }

    @Step("Проверить, что фотографии видны")
    public MyTravelsPage checkPhotos(List<PhotoModel> photoModels) {
        photoCardsCollection.shouldHave(exactPhotoCards(photoModels));
        return this;
    }

    @Step("Проверить, что фотографии видны")
    public MyTravelsPage checkPhotos(PhotoModel... photoModels) {
        photoCardsCollection.shouldHave(exactPhotoCards(photoModels));
        return this;
    }
}
