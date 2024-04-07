package guru.qa.rangiffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selectors.byTagAndText;

public class PhotoCard extends BaseComponent<PhotoCard> {
    private final SelenideElement likes = self.$("p.MuiTypography-root");
    private final SelenideElement likeButton = self.$("button[aria-label=like]");
    private final SelenideElement deleteButton = self.$(byTagAndText("button", "Delete"));
    private final SelenideElement editButton = self.$(byTagAndText("button", "Edit"));
    public PhotoCard(SelenideElement self) {
        super(self);
    }

    @Step("[PHOTO CARD] Проверить, что лайков [{amount}]")
    public void checkLikesAmount(int amount) {
        likes.shouldHave(exactText(amount + " likes"));
    }

    @Step("[PHOTO CARD] Нажать на лайк")
    public void clickLike() {
        likeButton.click();
    }

    @Step("[PHOTO CARD] Нажать [Delete]")
    public void clickDelete() {
        deleteButton.click();
    }

    @Step("[PHOTO CARD] Проверить, что нет кнопки [Delete]")
    public void checkDeleteNotExist() {
        deleteButton.shouldNot(exist);
    }

    @Step("[PHOTO CARD] Нажать [Edit]")
    public void clickEdit() {
        editButton.click();
    }

    @Step("[PHOTO CARD] Проверить, что нет кнопки [Edit]")
    public void checkEditNotExist() {
        editButton.shouldNot(exist);
    }
}
