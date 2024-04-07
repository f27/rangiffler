package guru.qa.rangiffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.model.PhotoModel;
import guru.qa.rangiffler.page.component.AddPhotoModal;
import guru.qa.rangiffler.page.component.EditPhotoModal;
import guru.qa.rangiffler.page.component.PhotoCard;
import guru.qa.rangiffler.page.component.WorldMap;
import io.qameta.allure.Step;
import lombok.Getter;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byTagAndText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static guru.qa.rangiffler.selenide.CustomConditions.photo;
import static guru.qa.rangiffler.selenide.CustomElementsConditions.exactPhotoCards;

public class MyTravelsPage extends BaseAuthorizedPage<MyTravelsPage> {

    @Getter
    private final AddPhotoModal addPhotoModal = new AddPhotoModal();
    @Getter
    private final EditPhotoModal editPhotoModal = new EditPhotoModal();
    @Getter
    private final WorldMap worldMap = new WorldMap();

    private final SelenideElement addPhotoButton = $(byTagAndText("button", "Add photo"));
    private final SelenideElement onlyMyTravelsButton = $(byTagAndText("button", "Only my travels"));
    private final SelenideElement withFriendsButton = $(byTagAndText("button", "With friends"));
    private final SelenideElement allCountriesButton = $(byTagAndText("button", "All countries"));
    private final SelenideElement photoPagination = $("[data-test-id=photoPagination]");
    private final SelenideElement previousPageButton = photoPagination.$(byTagAndText("button", "Previous"));
    private final SelenideElement nextPageButton = photoPagination.$(byTagAndText("button", "Next"));
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

    @Step("Проверить, что на странице [{amount}] фотографий")
    public MyTravelsPage checkAmountOfVisiblePhotos(int amount) {
        photoCardsCollection.shouldHave(size(amount));
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

    @Step("Проверить, что среди фотографий видна данная")
    public MyTravelsPage checkPhotoIsVisible(PhotoModel photoModel) {
        photoCardsCollection.findBy(photo(photoModel)).shouldBe(visible);
        return this;
    }

    @Step("Проверить, что фотография имеет [{likesAmount}] лайков")
    public MyTravelsPage checkPhotoHasAmountOfLikes(PhotoModel photoModel, int likesAmount) {
        new PhotoCard(photoCardsCollection.findBy(photo(photoModel)))
                .checkLikesAmount(likesAmount);
        return this;
    }

    @Step("Поставить лайк фотографии")
    public MyTravelsPage clickLikeToPhoto(PhotoModel photoModel) {
        new PhotoCard(photoCardsCollection.findBy(photo(photoModel)))
                .clickLike();
        return this;
    }

    @Step("Нажать кнопку [All countries]")
    public MyTravelsPage clickAllCountries() {
        allCountriesButton.click();
        return this;
    }

    @Step("Удалить фотографию")
    public MyTravelsPage deletePhoto(PhotoModel photoModel) {
        new PhotoCard(photoCardsCollection.findBy(photo(photoModel)))
                .clickDelete();
        return this;
    }

    @Step("Проверить, что у фотографии нет кнопки [Delete]")
    public MyTravelsPage checkPhotoHasNoDeleteButton(PhotoModel photoModel) {
        new PhotoCard(photoCardsCollection.findBy(photo(photoModel)))
                .checkDeleteNotExist();
        return this;
    }

    @Step("Нажать кнопку [Edit]")
    public MyTravelsPage clickEditPhoto(PhotoModel photoModel) {
        new PhotoCard(photoCardsCollection.findBy(photo(photoModel)))
                .clickEdit();
        return this;
    }

    @Step("Проверить, что у фотографии нет кнопки [Edit]")
    public MyTravelsPage checkPhotoHasNoEditButton(PhotoModel photoModel) {
        new PhotoCard(photoCardsCollection.findBy(photo(photoModel)))
                .checkEditNotExist();
        return this;
    }

    @Step("Проверить, что нет пагинации")
    public MyTravelsPage checkNoPagination() {
        photoPagination.shouldNot(exist);
        return this;
    }

    @Step("Проверить, что кнопка [Previous page] видна, но отключена")
    public MyTravelsPage checkPreviousButtonIsVisibleButDisabled() {
        previousPageButton.shouldBe(visible).shouldBe(disabled);
        return this;
    }

    @Step("Нажать [Previous page]")
    public MyTravelsPage clickPrevious() {
        previousPageButton.click();
        return this;
    }

    @Step("Проверить, что кнопка [Next page] видна, но отключена")
    public MyTravelsPage checkNextButtonIsVisibleButDisabled() {
        nextPageButton.shouldBe(visible).shouldBe(disabled);
        return this;
    }

    @Step("Нажать [Next page]")
    public MyTravelsPage clickNext() {
        nextPageButton.click();
        return this;
    }
}
