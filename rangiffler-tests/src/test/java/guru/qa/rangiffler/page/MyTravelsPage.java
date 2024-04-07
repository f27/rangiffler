package guru.qa.rangiffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.model.PhotoModel;
import guru.qa.rangiffler.page.component.AddPhotoModal;
import io.qameta.allure.Step;
import lombok.Getter;

import java.util.List;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selectors.byTagAndText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static guru.qa.rangiffler.selenide.CustomConditions.childElementWithText;
import static guru.qa.rangiffler.selenide.CustomElementsConditions.exactPhotoCards;

public class MyTravelsPage extends BaseAuthorizedPage<MyTravelsPage> {

    @Getter
    private final AddPhotoModal addPhotoModal = new AddPhotoModal();

    private final SelenideElement addPhotoButton = $(byTagAndText("button", "Add photo"));
    private final SelenideElement onlyMyTravelsButton = $(byTagAndText("button", "Only my travels"));
    private final SelenideElement withFriendsButton = $(byTagAndText("button", "With friends"));
    private final ElementsCollection photoCardsCollection = $$(".photo-card__container");

    @Step("Проверить, что успешно авторизовались")
    public void checkSuccessfullyAuthorized() {
        header.logoutButtonShouldBeVisible();
    }

    @Step("Нажать кнопку [Add photo]")
    public MyTravelsPage clickAddPhoto() {
        addPhotoButton.click();
        return this;
    }

    @Step("Переключить на [Only my travels]")
    public MyTravelsPage clickOnlyMyTravels() {
        onlyMyTravelsButton.click();
        return this;
    }

    @Step("Переключить на [With friends]")
    public MyTravelsPage clickWithFriends() {
        withFriendsButton.click();
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

    @Step("Проверить, что фотография с описанием [{description}] имеет [{likesAmount}] лайков")
    public MyTravelsPage checkPhotoWithDescriptionHasAmountOfLikes(String description, int likesAmount) {
        SelenideElement photoCard = photoCardsCollection.findBy(childElementWithText("p.photo-card__content", description));
        photoCard.$("p.MuiTypography-root").shouldHave(exactText(likesAmount + " likes"));
        return this;
    }

    @Step("Поставить лайк фотографии с описание [{description}]")
    public MyTravelsPage clickLikeToPhotoWithDescription(String description) {
        SelenideElement photoCard = photoCardsCollection.findBy(childElementWithText("p.photo-card__content", description));
        photoCard.$("button[aria-label=like]").click();
        return this;
    }
}
